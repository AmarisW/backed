/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.InputStream;
import java.util.Optional;

import com.gqtian.fileStorage.config.FileStorageProperties.HuaweiObsConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;
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
 * 👻 华为Obs
 *
 * @className HuaWeiObsPlatFrom
 * @author gqtian
 * @date 2025/3/21 17:52
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
    
    public HuaweiObsPlatFrom(HuaweiObsConfig huaweiObsConfig, FileStorageClientFactory<ObsClient> clientFactory)
    {
        this.platform = huaweiObsConfig.getPlatform();
        this.bucketName = huaweiObsConfig.getBucketName();
        this.basePath = huaweiObsConfig.getBasePath();
        this.domain = huaweiObsConfig.getDomain();
        this.clientFactory = clientFactory;
    }
    
    public ObsClient getClient() {
        return clientFactory.getClient();
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
        }
        catch (Exception e) {
            log.error("obs上传失败", e);
            return false;
        }
        return false;
    }
    
    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try {
            // 创建ObsClient实例
            ObsClient obsClient = getClient();
            // obs删除
            obsClient.deleteObject(bucketName, fileKey);
        }
        catch (ObsException e) {
            log.error("obs删除保存失败", e);
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
            // URL有效期，3600秒
            long expireSeconds = 3600L;
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
            request.setBucketName(bucketName);
            request.setObjectKey(fileKey);
            response = obsClient.createTemporarySignature(request);
        }
        catch (ObsException e) {
            log.error("obs获取临时签名失败", e);
        }
        return Optional.ofNullable(response).map(TemporarySignatureResponse::getSignedUrl).orElse(null);
    }
    
    @Override
    public void close() {
        clientFactory.close();
    }
}
