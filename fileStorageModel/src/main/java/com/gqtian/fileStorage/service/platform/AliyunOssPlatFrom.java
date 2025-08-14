/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import com.gqtian.fileStorage.config.FileStorageProperties.AliYunOssConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 阿里OSS平台
 *
 * @className AliyunOssPlatfrom
 * @author gqtian
 * @date 2025/3/21 10:10
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
    
    public AliyunOssPlatFrom(AliYunOssConfig aliYunOssConfig, FileStorageClientFactory<OSS> clientFactory)
    {
        this.platform = aliYunOssConfig.getPlatform();
        this.bucketName = aliYunOssConfig.getBucketName();
        this.basePath = aliYunOssConfig.getBasePath();
        this.domain = aliYunOssConfig.getDomain();
        this.clientFactory = clientFactory;
    }
    
    public OSS getClient() {
        return clientFactory.getClient();
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
        }
        catch (Exception e) {
            log.error("阿里云OSS文件上传失败", e);
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
        }
        catch (Exception e) {
            log.error("阿里云OSS文件删除失败", e);
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
        }
        catch (Exception e) {
            log.error("阿里云OSS文件预览失败", e);
        }
        return generateUrl;
    }
    
    @Override
    public void close() {
        clientFactory.close();
    }
}
