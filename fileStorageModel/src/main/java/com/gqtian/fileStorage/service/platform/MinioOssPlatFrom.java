/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.MinioConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.MultipartInitResult;
import com.gqtian.fileStorage.domain.MultipartPartETag;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;
import com.gqtian.fileStorage.service.s3.S3ProtocolSupport;

import io.minio.BucketExistsArgs;
import io.minio.CreateMultipartUploadResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadPartResponse;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MinIO 对象存储平台实现。
 * <p>
 * 默认使用 MinIO SDK 自有分片；配置 {@code s3-multipart-fallback: true} 时改走 AWS S3 SDK 协议分片（降级），
 * 日志中会出现 {@code [S3降级]} / {@code mode=S3降级}。
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class MinioOssPlatFrom implements FileStorageInterface {

    /** 平台编码 */
    private String platform;

    /** 桶 */
    private String bucketName;

    /** 基础上传路径 */
    private String basePath;

    /** 访问域名（访问地址前缀 domain+fileName 为实际的访问地址） */
    private String domain;

    /** 自动分片阈值（字节），预留给整文件自动分片场景 */
    private int multipartThreshold;

    /** 建议分片大小（字节） */
    private int multipartPartSize;

    /** Client 工厂 */
    private FileStorageClientFactory<MinioClient> clientFactory;

    /**
     * S3 协议分片降级；{@code s3-multipart-fallback=true} 时非空，分片优先走该通道
     */
    private S3ProtocolSupport s3ProtocolSupport;

    public MinioOssPlatFrom(MinioConfig config, FileStorageClientFactory<MinioClient> clientFactory) {
        platform = config.getPlatform();
        bucketName = config.getBucketName();
        domain = config.getDomain();
        basePath = config.getBasePath();
        multipartThreshold = config.getMultipartThreshold();
        multipartPartSize = config.getMultipartPartSize();
        this.clientFactory = clientFactory;
        // true：强制用 AWS S3 SDK 分片；false：MinIO 自有分片
        if (S3ProtocolSupport.shouldEnable(config)) {
            this.s3ProtocolSupport = S3ProtocolSupport.create(platform, config, config.getMultipartPartSize(),
                config.getMultipartThreshold());
            log.info("【文件服务】[S3降级] 平台已挂载分片降级通道 | platform=minio | code={}", platform);
        }
    }

    public MinioClient getClient() {
        return clientFactory.getClient();
    }

    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try (InputStream inputStream = fileStorageInfo.getInputStream()) {
            MinioClient client = getClient();
            ensureBucket(client);
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileKey)
                .stream(inputStream, inputStream.available(), -1).build());
            return true;
        } catch (Exception e) {
            log.error("【文件服务】minio 文件上传失败", e);
        }
        return false;
    }

    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        MinioClient client = getClient();
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileKey).build());
            return true;
        } catch (Exception e) {
            log.error("【文件服务】minio 文件删除失败", e);
            throw new GlobalException(fileKey + "文件删除失败");
        }
    }

    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        String generateUrl;
        try {
            MinioClient client = getClient();
            generateUrl = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET)
                .bucket(bucketName).object(fileKey).expiry(1, TimeUnit.DAYS).build());
        } catch (Exception e) {
            log.error("【文件服务】minio 文件预览失败", e);
            throw new GlobalException(fileKey + "文件预览失败");
        }
        return generateUrl;
    }

    @Override
    public S3ProtocolSupport s3ProtocolSupport() {
        return s3ProtocolSupport;
    }

    @Override
    public boolean supportMultipartUpload() {
        return true;
    }

    @Override
    public MultipartInitResult initiateMultipartUpload(FileStorageInfo fileStorageInfo) {
        // 开启 s3-multipart-fallback 时优先 AWS S3 SDK
        if (s3ProtocolSupport != null) {
            return s3ProtocolSupport.initiateMultipartUpload(fileStorageInfo);
        }
        String filename = resolveFilename(fileStorageInfo);
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), filename);
        try {
            MinioClient client = getClient();
            ensureBucket(client);
            // MinIO 自有分片（SDK protected API）
            String uploadId = new MinioSdkAccess(client).initiateMultipartUpload(bucketName, fileKey);
            log.info("【文件服务】minio 初始化分片成功 | objectKey={} | uploadId={}", fileKey, uploadId);
            return MultipartInitResult.builder().platform(platform).uploadId(uploadId).objectKey(fileKey)
                .filename(filename).path(fileStorageInfo.getPath()).build();
        } catch (Exception e) {
            log.error("【文件服务】minio 初始化分片失败 | objectKey={}", fileKey, e);
            throw new GlobalException("【文件服务】minio 初始化分片失败", e);
        }
    }

    @Override
    public MultipartPartETag uploadPart(FileStorageInfo fileStorageInfo, String uploadId, int partNumber,
        InputStream partData, long partSize) {
        if (s3ProtocolSupport != null) {
            return s3ProtocolSupport.uploadPart(fileStorageInfo, uploadId, partNumber, partData, partSize);
        }
        validatePartArgs(uploadId, partNumber, partSize);
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            String etag = new MinioSdkAccess(getClient()).uploadPart(bucketName, fileKey, uploadId, partNumber,
                partData, (int)partSize);
            log.info("【文件服务】minio 分片上传成功 | objectKey={} | partNumber={} | size={}", fileKey, partNumber,
                partSize);
            return MultipartPartETag.builder().partNumber(partNumber).etag(etag).build();
        } catch (Exception e) {
            log.error("【文件服务】minio 分片上传失败 | objectKey={} | partNumber={}", fileKey, partNumber, e);
            throw new GlobalException("【文件服务】minio 分片上传失败", e);
        }
    }

    @Override
    public boolean completeMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId,
        List<MultipartPartETag> parts) {
        if (s3ProtocolSupport != null) {
            return s3ProtocolSupport.completeMultipartUpload(fileStorageInfo, uploadId, parts);
        }
        if (StringUtils.isBlank(uploadId)) {
            throw new GlobalException("【文件服务】uploadId 不能为空");
        }
        if (parts == null || parts.isEmpty()) {
            throw new GlobalException("【文件服务】分片列表不能为空");
        }
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            // 按分片号升序合并，符合 S3/MinIO 要求
            Part[] minioParts = parts.stream().sorted(Comparator.comparingInt(MultipartPartETag::getPartNumber))
                .map(p -> new Part(p.getPartNumber(), p.getEtag())).toArray(Part[]::new);
            new MinioSdkAccess(getClient()).completeMultipartUpload(bucketName, fileKey, uploadId, minioParts);
            log.info("【文件服务】minio 合并分片成功 | objectKey={} | parts={}", fileKey, minioParts.length);
            return true;
        } catch (Exception e) {
            log.error("【文件服务】minio 合并分片失败 | objectKey={} | uploadId={}", fileKey, uploadId, e);
            throw new GlobalException("【文件服务】minio 合并分片失败", e);
        }
    }

    @Override
    public boolean abortMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId) {
        if (s3ProtocolSupport != null) {
            return s3ProtocolSupport.abortMultipartUpload(fileStorageInfo, uploadId);
        }
        if (StringUtils.isBlank(uploadId)) {
            throw new GlobalException("【文件服务】uploadId 不能为空");
        }
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            new MinioSdkAccess(getClient()).abortMultipartUpload(bucketName, fileKey, uploadId);
            log.info("【文件服务】minio 取消分片成功 | objectKey={} | uploadId={}", fileKey, uploadId);
            return true;
        } catch (Exception e) {
            log.error("【文件服务】minio 取消分片失败 | objectKey={} | uploadId={}", fileKey, uploadId, e);
            throw new GlobalException("【文件服务】minio 取消分片失败", e);
        }
    }

    @Override
    public void close() {
        if (s3ProtocolSupport != null) {
            s3ProtocolSupport.close();
            s3ProtocolSupport = null;
        }
        clientFactory.close();
    }

    /** 确保存储桶存在 */
    private void ensureBucket(MinioClient client) throws Exception {
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /** 解析文件名：优先使用传入 filename，否则按 fileType 生成 */
    private String resolveFilename(FileStorageInfo fileStorageInfo) {
        if (StringUtils.isNotBlank(fileStorageInfo.getFilename())) {
            return fileStorageInfo.getFilename();
        }
        if (StringUtils.isNotBlank(fileStorageInfo.getFileType())) {
            String generated = generateFilename(fileStorageInfo.getFileType());
            fileStorageInfo.setFilename(generated);
            return generated;
        }
        throw new GlobalException("【文件服务】分片上传需指定 filename 或 fileType");
    }

    /** 校验分片参数 */
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
        // MinIO SDK uploadPart 的 length 为 int
        if (partSize > Integer.MAX_VALUE) {
            throw new GlobalException("【文件服务】单分片大小不能超过 " + Integer.MAX_VALUE + " 字节");
        }
    }

    /**
     * 访问 MinIO SDK 中 protected 的分片 API。
     * <p>
     * 仅作为平台实现细节，不对外暴露新的 Client 类型；对外统一走 {@link FileStorageInterface}。
     */
    private static final class MinioSdkAccess extends MinioClient {

        private MinioSdkAccess(MinioClient client) {
            super(client);
        }

        private String initiateMultipartUpload(String bucket, String object) throws Exception {
            CreateMultipartUploadResponse response =
                createMultipartUpload(bucket, null, object, null, null);
            return response.result().uploadId();
        }

        private String uploadPart(String bucket, String object, String uploadId, int partNumber,
            InputStream data, int partSize) throws Exception {
            // SDK 要求 BufferedInputStream / byte[] 等类型
            BufferedInputStream buffered =
                data instanceof BufferedInputStream ? (BufferedInputStream)data
                    : new BufferedInputStream(data);
            UploadPartResponse response =
                uploadPart(bucket, null, object, buffered, partSize, uploadId, partNumber, null, null);
            return response.etag();
        }

        private void completeMultipartUpload(String bucket, String object, String uploadId, Part[] parts)
            throws Exception {
            completeMultipartUpload(bucket, null, object, uploadId, parts, null, null);
        }

        private void abortMultipartUpload(String bucket, String object, String uploadId) throws Exception {
            abortMultipartUpload(bucket, null, object, uploadId, null, null);
        }
    }
}
