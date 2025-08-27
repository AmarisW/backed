/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.factory;

import com.gqtian.fileStorage.config.FileStorageProperties.HuaweiObsConfig;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.obs.services.ObsClient;

import cn.hutool.core.io.IoUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 👻 华为Obs 创建工厂
 *
 * @className HuaWeiObsClientFactory
 * @author gqtian
 * @date 2025/3/21 17:47
 */
@Getter
@Setter
@NoArgsConstructor
public class HuaweiObsClientFactory implements FileStorageClientFactory<ObsClient> {

    /** 平台编码 */
    private String platform;

    /** 访问密钥 */
    private String accessKey;

    /** 密钥 */
    private String secretKey;

    /** 桶名称 */
    private String bucketName;

    /** 访问域名 （[ip + 端口] | 域名） */
    private String endPoint;

    /** 基础上传路径 */
    private String basePath;

    /** 访问域名（访问地址前缀 domain+fileName 为实际的访问地址） */
    private String domain;

    /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
    private int multipartThreshold;

    /** 自动分片上传时每个分片大小，默认 32MB */
    private int multipartPartSize;

    /** 客户端 */
    private volatile ObsClient client;

    public HuaweiObsClientFactory(HuaweiObsConfig config) {
        this.platform = config.getPlatform();
        this.accessKey = config.getAccessKey();
        this.secretKey = config.getSecretKey();
        this.bucketName = config.getBucketName();
        this.endPoint = config.getEndPoint();
        this.basePath = config.getBasePath();
        this.domain = config.getDomain();
        this.multipartThreshold = config.getMultipartThreshold();
        this.multipartPartSize = config.getMultipartPartSize();
    }

    @Override
    public ObsClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = new ObsClient(accessKey, secretKey, endPoint);
                }
            }
        }
        return client;
    }

    @Override
    public void close() {
        IoUtil.close(client);
        client = null;
    }
}
