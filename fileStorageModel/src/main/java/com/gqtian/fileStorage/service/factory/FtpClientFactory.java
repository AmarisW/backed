/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.factory;

import java.nio.charset.Charset;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.FtpOssConfig;
import com.gqtian.fileStorage.service.FileStorageClientFactory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import cn.hutool.extra.ftp.FtpMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** FTP 存储平台的 Client 工厂，使用了对象池缓存，性能更高 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class FtpClientFactory implements FileStorageClientFactory<Ftp> {
    
    private String platform;
    
    private String endPoint;
    
    private String accessKey;
    
    private String secretKey;
    
    private Charset charset;
    
    private Integer connectionTimeout;
    
    private Long soTimeout;
    
    private String serverLanguageCode;
    
    private String systemKey;
    
    private Boolean isActive;
    
    private GenericObjectPoolConfig<Ftp> poolConfig;
    
    private volatile GenericObjectPool<Ftp> pool;
    
    public FtpClientFactory(FtpOssConfig ftpOssConfig)
    {
        platform = ftpOssConfig.getPlatform();
        endPoint = ftpOssConfig.getEndPoint();
        accessKey = ftpOssConfig.getAccessKey();
        secretKey = ftpOssConfig.getSecretKey();
        charset = ftpOssConfig.getCharset();
        connectionTimeout = ftpOssConfig.getConnectionTimeout();
        soTimeout = ftpOssConfig.getSoTimeout();
        serverLanguageCode = ftpOssConfig.getServerLanguageCode();
        systemKey = ftpOssConfig.getSystemKey();
        isActive = ftpOssConfig.getIsActive();
        poolConfig = ftpOssConfig.getPool().toGenericObjectPoolConfig();
    }
    
    @Override
    public Ftp getClient() {
        try {
            if (pool == null) {
                synchronized (this) {
                    if (pool == null) {
                        pool = new GenericObjectPool<Ftp>(new FtpPooledObjectFactory(this), poolConfig);
                    }
                }
            }
            return pool.borrowObject();
        }
        catch (Exception e) {
            throw new GlobalException("【文件服务】获取 FTP Client 失败！", e);
        }
    }
    
    @Override
    public void close() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }
    
    @Override
    public void returnClient(Ftp ftp) {
        try {
            pool.returnObject(ftp);
        }
        catch (Exception e) {
            throw new GlobalException("【文件服务】归还 FTP Client 失败！", e);
        }
    }
    
    /** Ftp 的对象池包装的工厂 */
    @Slf4j
    @AllArgsConstructor
    public static class FtpPooledObjectFactory extends BasePooledObjectFactory<Ftp> {
        
        private FtpClientFactory factory;
        
        @Override
        public Ftp create() {
            if (factory == null) {
                throw new GlobalException("【文件服务】FTP 连接失败！config 不能为空");
            }
            
            try {
                String[] split = factory.getEndPoint().split(":");
                return new Ftp(
                    FtpConfig.create()
                        .setHost(split[0])
                        .setPort(Integer.parseInt(split[1]))
                        .setUser(factory.getAccessKey())
                        .setPassword(factory.getSecretKey())
                        .setCharset(factory.getCharset())
                        .setConnectionTimeout(factory.getConnectionTimeout())
                        .setSoTimeout(factory.getSoTimeout())
                        .setServerLanguageCode(factory.getServerLanguageCode())
                        .setSystemKey(factory.getSystemKey()),
                    factory.getIsActive() ? FtpMode.Active : FtpMode.Passive);
            }
            catch (Exception e) {
                throw new GlobalException("【文件服务】FTP 连接失败！platform：" + factory.getPlatform(), e);
            }
        }
        
        @Override
        public PooledObject<Ftp> wrap(Ftp ftp) {
            return new DefaultPooledObject<>(ftp);
        }
        
        @Override
        public boolean validateObject(PooledObject<Ftp> p) {
            try {
                p.getObject().cd(StrUtil.DOT);
                return true;
            }
            catch (FtpException e) {
                log.error("【文件服务】验证 Ftp 对象失败", e);
                return false;
            }
        }
        
        @Override
        public void destroyObject(PooledObject<Ftp> p) {
            try {
                p.getObject().close();
            }
            catch (Exception e) {
                throw new GlobalException("【文件服务】销毁 Ftp 对象失败！", e);
            }
        }
    }
}
