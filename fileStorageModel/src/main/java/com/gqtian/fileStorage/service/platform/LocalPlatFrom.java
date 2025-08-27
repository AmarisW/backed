/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service.platform;

import java.io.File;
import java.io.InputStream;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties.LocalConfig;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageInterface;

import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 本地目录
 *
 * @className LocalPlatFrom
 * @author gqtian
 * @date 2025/3/25 11:14
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class LocalPlatFrom implements FileStorageInterface {

    /** 基础路径 */
    private String basePath;

    /** 平台 */
    private String platform;

    /** 访问地址前缀 */
    private String domain;

    public LocalPlatFrom(LocalConfig config) {
        platform = config.getPlatform();
        basePath = config.getBasePath();
        domain = config.getDomain();
    }

    @Override
    public boolean upload(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());
        try (InputStream inputStream = fileStorageInfo.getInputStream())

        {
            File buildFile = FileUtil.touch(fileKey);
            FileUtil.writeFromStream(inputStream, buildFile);
        } catch (Exception e) {
            log.error("【文件服务】本地文件上传失败", e);
            FileUtil.del(fileKey);
            throw new GlobalException("【文件服务】本地文件上传失败");
        }
        return true;
    }

    @Override
    public boolean delete(FileStorageInfo fileStorageInfo) {
        String fileKey = getStorageLocation(basePath, fileStorageInfo.getPath(), fileStorageInfo.getFilename());

        try {
            return FileUtil.del(fileKey);
        } catch (Exception e) {
            log.error("【文件服务】本地文件删除失败", e);
            throw new GlobalException("【文件服务】本地文件删除失败");
        }
    }

    @Override
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        return "";
    }
}
