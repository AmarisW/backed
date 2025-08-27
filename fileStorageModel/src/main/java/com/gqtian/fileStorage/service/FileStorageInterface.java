/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service;

import java.util.Date;

import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.utils.Tools;

import cn.hutool.core.util.StrUtil;

/** 文件存储接口，对应各个平台 */
public interface FileStorageInterface extends AutoCloseable {

    /** 获取平台 */
    String getPlatform();

    /**
     * 获取文件全路径（相对存储平台的存储路径）
     *
     * @param basePath 基础路径（根路径）
     * @param path 文件拓展路径
     * @param filename 文件名
     * @return 文件存储位置
     */
    default String getStorageLocation(String basePath, String path, String filename) {
        return Tools.getNotNull(basePath, StrUtil.EMPTY) + Tools.getNotNull(path, StrUtil.EMPTY)
            + Tools.getNotNull(filename, StrUtil.EMPTY);
    }

    /**
     * 生成文件名（时间戳+文件类型）
     *
     * @param fileType 文件类型
     * @return 文件名
     */
    default String generateFilename(String fileType) {
        return new Date().getTime() + "." + fileType;
    }

    /** 保存文件 */
    boolean upload(FileStorageInfo fileStorageInfo) throws Exception;

    /** 删除文件 */
    boolean delete(FileStorageInfo fileStorageInfo);

    /** 获取预览Url */
    String getPreviewUrl(FileStorageInfo fileStorageInfo);

    /** 关闭相关资源 */
    default void close() {}
    /** TODO 文件下载 */

    /** TODO 分片上传 */
}
