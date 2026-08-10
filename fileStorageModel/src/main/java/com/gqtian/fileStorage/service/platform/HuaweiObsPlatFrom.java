/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.InputStream;
import java.util.Optional;

import com.gqtian.fileStorage.config.FileStorageProperties.HuaweiObsConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;
import com.gqtian.fileStorage.service.s3.S3ProtocolSupport;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 华为 OBS 平台。
 * <p>
 * 尚未实现自有分片时，可配置 {@code s3MultipartFallback=true} 走 S3 协议降级分片。
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class HuaweiObsPlatFrom implements FileStorageInterface {

    /** 平台编码 */
    private String platform;

    /** 桶 */
    private String bucketName;

    /** 基础上传路径 */
    private String basePath;

    /** 访问域名（访问地址前缀 domain+fileName 为实际的访问地址） */
    private String domain;

    /** 工厂（包含配置信息及连接工具） */
    private FileStorageClientFactory<ObsClient> clientFactory;

    /** S3 协议分片降级；可为 null */
    private S3ProtocolSupport s3ProtocolSupport;

    public HuaweiObsPlatFrom(HuaweiObsConfig huaweiObsConfig, FileStorageClientFactory<ObsClient> clientFactory) {
        this.platform = huaweiObsConfig.getPlatform();
        this.bucketName = huaweiObsConfig.getBucketName();
        this.basePath = huaweiObsConfig.getBasePath();
        this.domain = huaweiObsConfig.getDomain();
        this.clientFactory = clientFactory;
        if (S3ProtocolSupport.shouldEnable(huaweiObsConfig)) {
            this.s3ProtocolSupport = S3ProtocolSupport.create(platform, huaweiObsConfig,
                huaweiObsConfig.getMultipartPartSize());
            log.info("【文件服务】华为 OBS 已启用 S3 协议分片降级 | platform={}", platform);
        }
    }

    public ObsClient getClient() {
        return clientFactory.getClient();
    }

    @Override
    public S3ProtocolSupport s3ProtocolSupport() {
        return s3ProtocolSupport;
    }

    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try (InputStream inputStream = fileStorageInfo.getInputStream()) {
            ObsClient obsClient = getClient();
            PutObjectResult putObjectResult = obsClient.putObject(bucketName, fileKey, inputStream);
            if (putObjectResult != null && putObjectResult.getStatusCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            log.error("【文件服务】obs上传失败", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            ObsClient obsClient = getClient();
            obsClient.deleteObject(bucketName, fileKey);
        } catch (ObsException e) {
            log.error("【文件服务】obs删除保存失败", e);
            return false;
        }
        return true;
    }

    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        TemporarySignatureResponse response = null;
        try {
            ObsClient obsClient = getClient();
            long expireSeconds = 3600L;
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
            request.setBucketName(bucketName);
            request.setObjectKey(fileKey);
            response = obsClient.createTemporarySignature(request);
        } catch (ObsException e) {
            log.error("【文件服务】obs获取临时签名失败", e);
        }
        return Optional.ofNullable(response).map(TemporarySignatureResponse::getSignedUrl).orElse(null);
    }

    @Override
    public void close() {
        if (s3ProtocolSupport != null) {
            s3ProtocolSupport.close();
            s3ProtocolSupport = null;
        }
        clientFactory.close();
    }
}
