/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.InputStream;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.FtpOssConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageClientFactory;
import com.gqtian.fileStorage.service.FileStorageInterface;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 ftp客户端平台
 *
 * @className FtpPlatFrom
 * @author gqtian
 * @date 2025/3/25 11:14
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class FtpPlatFrom implements FileStorageInterface {
    
    private String platform;
    
    private String domain;
    
    private String basePath;
    
    private FileStorageClientFactory<Ftp> clientFactory;
    
    public FtpPlatFrom(FtpOssConfig config, FileStorageClientFactory<Ftp> clientFactory)
    {
        this.platform = config.getPlatform();
        this.basePath = config.getBasePath();
        this.domain = config.getDomain();
        this.clientFactory = clientFactory;
    }
    
    /** 获取 Client ，使用完后需要归还 */
    public Ftp getClient() {
        return clientFactory.getClient();
    }
    
    /** 归还 Client */
    public void returnClient(Ftp client) {
        clientFactory.returnClient(client);
    }
    
    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        Ftp client = getClient();
        // 重置 FTP 路径 不然会重复建造目录
        client.cd(StrUtil.SLASH);
        // 获取文件上传的绝对路径
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), null);
        try (InputStream inputStream = fileStorageInfo.getInputStream()) {
            return client.upload(fileKey, fileStorageInfo.getFilename(), inputStream);
        }
        catch (Exception e) {
            client.delFile(fileKey + fileStorageInfo.getFilename());
            log.error("【文件服务】ftp文件上传失败", e);
            throw new GlobalException("【文件服务】ftp文件上传失败");
        }
        finally {
            returnClient(client);
        }
    }
    
    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        // 文件全路径
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        Ftp client = getClient();
        return client.delFile(fileKey);
    }
    
    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        return domain + StrUtil.SLASH
            + getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
    }
    
    @Override
    public void close() {
        clientFactory.close();
    }
}
