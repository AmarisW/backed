/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.s3;

import java.io.InputStream;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.BaseConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.MultipartInitResult;
import com.gqtian.fileStorage.domain.MultipartPartETag;
import com.gqtian.fileStorage.utils.Tools;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

/**
 * S3 协议分片能力（降级用）。
 * <p>
 * 不作为独立存储平台；当对象存储支持 S3 协议、但平台未实现自有分片封装时，
 * 由 {@link com.gqtian.fileStorage.service.FileStorageInterface} 默认分片方法委托本类执行。
 */
@Slf4j
@Getter
public class S3ProtocolSupport implements AutoCloseable {

    /** 所属业务平台编码（如 aliYunOss / minio，仅用于回填结果） */
    private final String platform;

    private final String bucketName;

    private final String basePath;

    /** 建议分片大小 */
    private final int multipartPartSize;

    private final S3Client s3Client;

    private S3ProtocolSupport(String platform, String bucketName, String basePath, int multipartPartSize,
        S3Client s3Client) {
        this.platform = platform;
        this.bucketName = bucketName;
        this.basePath = basePath;
        this.multipartPartSize = multipartPartSize;
        this.s3Client = s3Client;
    }

    /**
     * 根据平台通用配置构建 S3 协议支持（用于分片降级）。
     *
     * @param platform 业务平台编码
     * @param config 含 endpoint/密钥/桶/region/pathStyle 等
     * @param multipartPartSize 建议分片大小
     * @return S3ProtocolSupport
     */
    public static S3ProtocolSupport create(String platform, BaseConfig config, int multipartPartSize) {
        if (StringUtils.isAnyBlank(config.getAccessKey(), config.getSecretKey(), config.getBucketName())) {
            throw new GlobalException("【文件服务】启用 S3 分片降级时 accessKey/secretKey/bucketName 不能为空");
        }
        if (StringUtils.isBlank(config.getEndPoint())) {
            throw new GlobalException("【文件服务】启用 S3 分片降级时 endPoint 不能为空");
        }
        S3Client client = buildClient(config);
        String platformCode = StringUtils.defaultIfBlank(platform, config.getPlatform());
        int partSize = multipartPartSize > 0 ? multipartPartSize : 32 * 1024 * 1024;
        return new S3ProtocolSupport(platformCode, config.getBucketName(), config.getBasePath(), partSize, client);
    }

    /**
     * 是否应根据配置启用 S3 分片降级。
     *
     * @param config 平台配置
     * @return true 表示应创建 {@link S3ProtocolSupport}
     */
    public static boolean shouldEnable(BaseConfig config) {
        return config != null && config.isS3MultipartFallback();
    }

    /** CreateMultipartUpload */
    public MultipartInitResult initiateMultipartUpload(FileStorageInfo fileStorageInfo) {
        String filename = resolveFilename(fileStorageInfo);
        String objectKey = objectKey(fileStorageInfo.getPath(), filename);
        try {
            CreateMultipartUploadResponse response = s3Client.createMultipartUpload(CreateMultipartUploadRequest
                .builder().bucket(bucketName).key(objectKey).build());
            String uploadId = response.uploadId();
            log.info("【文件服务】S3协议分片初始化成功 | platform={} | objectKey={} | uploadId={}", platform, objectKey,
                uploadId);
            return MultipartInitResult.builder().platform(platform).uploadId(uploadId).objectKey(objectKey)
                .filename(filename).path(fileStorageInfo.getPath()).build();
        } catch (Exception e) {
            log.error("【文件服务】S3协议分片初始化失败 | platform={} | objectKey={}", platform, objectKey, e);
            throw new GlobalException("【文件服务】S3协议分片初始化失败", e);
        }
    }

    /** UploadPart */
    public MultipartPartETag uploadPart(FileStorageInfo fileStorageInfo, String uploadId, int partNumber,
        InputStream partData, long partSize) {
        validatePartArgs(uploadId, partNumber, partSize);
        String objectKey = objectKey(fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            UploadPartRequest request = UploadPartRequest.builder().bucket(bucketName).key(objectKey)
                .uploadId(uploadId).partNumber(partNumber).contentLength(partSize).build();
            UploadPartResponse response =
                s3Client.uploadPart(request, RequestBody.fromInputStream(partData, partSize));
            log.info("【文件服务】S3协议分片上传成功 | platform={} | objectKey={} | partNumber={}", platform, objectKey,
                partNumber);
            return MultipartPartETag.builder().partNumber(partNumber).etag(response.eTag()).build();
        } catch (Exception e) {
            log.error("【文件服务】S3协议分片上传失败 | platform={} | objectKey={} | partNumber={}", platform, objectKey,
                partNumber, e);
            throw new GlobalException("【文件服务】S3协议分片上传失败", e);
        }
    }

    /** CompleteMultipartUpload */
    public boolean completeMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId,
        List<MultipartPartETag> parts) {
        if (StringUtils.isBlank(uploadId)) {
            throw new GlobalException("【文件服务】uploadId 不能为空");
        }
        if (parts == null || parts.isEmpty()) {
            throw new GlobalException("【文件服务】分片列表不能为空");
        }
        String objectKey = objectKey(fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            List<CompletedPart> completedParts = parts.stream()
                .sorted(Comparator.comparingInt(MultipartPartETag::getPartNumber))
                .map(p -> CompletedPart.builder().partNumber(p.getPartNumber()).eTag(p.getEtag()).build())
                .collect(Collectors.toList());
            s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder().bucket(bucketName)
                .key(objectKey).uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build()).build());
            log.info("【文件服务】S3协议合并分片成功 | platform={} | objectKey={} | parts={}", platform, objectKey,
                completedParts.size());
            return true;
        } catch (Exception e) {
            log.error("【文件服务】S3协议合并分片失败 | platform={} | objectKey={} | uploadId={}", platform, objectKey,
                uploadId, e);
            throw new GlobalException("【文件服务】S3协议合并分片失败", e);
        }
    }

    /** AbortMultipartUpload */
    public boolean abortMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId) {
        if (StringUtils.isBlank(uploadId)) {
            throw new GlobalException("【文件服务】uploadId 不能为空");
        }
        String objectKey = objectKey(fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder().bucket(bucketName).key(objectKey)
                .uploadId(uploadId).build());
            log.info("【文件服务】S3协议取消分片成功 | platform={} | objectKey={} | uploadId={}", platform, objectKey,
                uploadId);
            return true;
        } catch (Exception e) {
            log.error("【文件服务】S3协议取消分片失败 | platform={} | objectKey={} | uploadId={}", platform, objectKey,
                uploadId, e);
            throw new GlobalException("【文件服务】S3协议取消分片失败", e);
        }
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    private String objectKey(String path, String filename) {
        return Tools.getNotNull(basePath, StrUtil.EMPTY) + Tools.getNotNull(path, StrUtil.EMPTY)
            + Tools.getNotNull(filename, StrUtil.EMPTY);
    }

    private String resolveFilename(FileStorageInfo fileStorageInfo) {
        if (StringUtils.isNotBlank(fileStorageInfo.getFilename())) {
            return fileStorageInfo.getFilename();
        }
        if (StringUtils.isNotBlank(fileStorageInfo.getFileType())) {
            String generated = System.currentTimeMillis() + "." + fileStorageInfo.getFileType();
            fileStorageInfo.setFilename(generated);
            return generated;
        }
        throw new GlobalException("【文件服务】分片上传需指定 filename 或 fileType");
    }

    private void validatePartArgs(String uploadId, int partNumber, long partSize) {
        if (StringUtils.isBlank(uploadId)) {
            throw new GlobalException("【文件服务】uploadId 不能为空");
        }
        if (partNumber < 1 || partNumber > 10000) {
            throw new GlobalException("【文件服务】partNumber 需在 1~10000 之间");
        }
        if (partSize <= 0) {
            throw new GlobalException("【文件服务】partSize 必须大于 0");
        }
    }

    private static S3Client buildClient(BaseConfig config) {
        String region = StringUtils.defaultIfBlank(config.getRegion(), "us-east-1");
        var builder = S3Client.builder().region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider
                .create(AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())))
            .serviceConfiguration(
                S3Configuration.builder().pathStyleAccessEnabled(config.isPathStyleAccess()).build());
        URI endpoint = resolveEndpointUri(config.getEndPoint());
        if (endpoint != null) {
            builder.endpointOverride(endpoint);
        }
        return builder.build();
    }

    private static URI resolveEndpointUri(String endPoint) {
        if (StringUtils.isBlank(endPoint)) {
            return null;
        }
        String endpoint = endPoint.trim();
        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
            endpoint = "https://" + endpoint;
        }
        return URI.create(endpoint);
    }
}
