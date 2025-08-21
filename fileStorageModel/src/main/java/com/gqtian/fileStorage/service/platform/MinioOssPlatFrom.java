/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.MinioConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** 阿里云 OSS 存储 */
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
    
    /** 工厂（包含配置信息及连接工具） */
    private FileStorageClientFactory<MinioClient> clientFactory;
    
    public MinioOssPlatFrom(MinioConfig config, FileStorageClientFactory<MinioClient> clientFactory)
    {
        platform = config.getPlatform();
        bucketName = config.getBucketName();
        domain = config.getDomain();
        basePath = config.getBasePath();
        this.clientFactory = clientFactory;
    }
    
    public MinioClient getClient() {
        return clientFactory.getClient();
    }
    
    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try (InputStream inputStream = fileStorageInfo.getInputStream())
        
        {
            MinioClient client = getClient();
            // 判断存储桶是否存在
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            // 获取文件后缀类型
            // String contentType =
            // fileStorageInfo.getFilename().substring(fileStorageInfo.getFilename().lastIndexOf(".") +
            // 1);
            // 上传
            client.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileKey)
                .stream(inputStream, inputStream.available(), -1)
                // 文件上传的类型，如果不指定，那么每次访问时都要先下载文件
                // .contentType(contentType)
                .build());
            return true;
        }
        catch (Exception e) {
            log.error("【文件服务】minioOss文件上传失败", e);
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
        }
        catch (Exception e) {
            log.error("【文件服务】minioOss文件删除失败", e);
            throw new GlobalException(fileKey + "文件删除失败");
        }
    }
    
    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        String generateUrl;
        try {
            MinioClient client = getClient();
            generateUrl = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileKey)
                .expiry(1, TimeUnit.DAYS)
                .build());
        }
        catch (Exception e) {
            log.error("【文件服务】minioOss文件预览失败", e);
            throw new GlobalException(fileKey + "文件预览失败");
        }
        return generateUrl;
    }
    
    @Override
    public void close() {
        clientFactory.close();
    }
}
