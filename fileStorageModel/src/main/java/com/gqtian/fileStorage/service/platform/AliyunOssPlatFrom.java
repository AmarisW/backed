/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.gqtian.fileStorage.config.FileStorageProperties.AliYunOssConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;
import com.gqtian.fileStorage.service.s3.S3ProtocolSupport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云 OSS 平台。
 * <p>
 * 尚未实现自有分片时，可配置 {@code s3MultipartFallback=true} 走 S3 协议降级分片
 *（需 endpoint 支持 S3 兼容 API）。
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class AliyunOssPlatFrom implements FileStorageInterface {

    /** 平台编码 */
    private String platform;

    /** 桶 */
    private String bucketName;

    /** 基础上传路径 */
    private String basePath;

    /** 访问域名（访问地址前缀 domain+fileName 为实际的访问地址） */
    private String domain;

    /** 工厂（包含配置信息及连接工具） */
    private FileStorageClientFactory<OSS> clientFactory;

    /**
     * S3 协议分片降级（未实现阿里云自有分片时使用）；可为 null
     */
    private S3ProtocolSupport s3ProtocolSupport;

    public AliyunOssPlatFrom(AliYunOssConfig aliYunOssConfig, FileStorageClientFactory<OSS> clientFactory) {
        this.platform = aliYunOssConfig.getPlatform();
        this.bucketName = aliYunOssConfig.getBucketName();
        this.basePath = aliYunOssConfig.getBasePath();
        this.domain = aliYunOssConfig.getDomain();
        this.clientFactory = clientFactory;
        // 无自有分片封装时，按配置降级到 S3 协议分片
        if (S3ProtocolSupport.shouldEnable(aliYunOssConfig)) {
            this.s3ProtocolSupport = S3ProtocolSupport.create(platform, aliYunOssConfig,
                aliYunOssConfig.getMultipartPartSize());
            log.info("【文件服务】阿里云 OSS 已启用 S3 协议分片降级 | platform={}", platform);
        }
    }

    public OSS getClient() {
        return clientFactory.getClient();
    }

    @Override
    public S3ProtocolSupport s3ProtocolSupport() {
        return s3ProtocolSupport;
    }

    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        try (InputStream inputStream = fileStorageInfo.getInputStream()) {
            OSS client = getClient();
            String fileName = fileStorageInfo.getFilename();
            String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileName);
            // 执行上传
            PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, inputStream);
            client.putObject(request);
        } catch (Exception e) {
            log.error("【文件服务】阿里云OSS文件上传失败", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            OSS client = getClient();
            client.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            log.error("【文件服务】阿里云OSS文件删除失败", e);
            return false;
        }
        return true;
    }

    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        String generateUrl = StringUtils.EMPTY;
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            OSS client = getClient();
            // 30年 有效期
            Date expiration = new Date(System.currentTimeMillis() + 946080000L * 1000);
            generateUrl = client.generatePresignedUrl(bucketName, fileKey, expiration).toString();
        } catch (Exception e) {
            log.error("【文件服务】阿里云OSS文件预览失败", e);
        }
        return generateUrl;
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
