/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.MultipartInitResult;
import com.gqtian.fileStorage.domain.MultipartPartETag;
import com.gqtian.fileStorage.service.s3.S3ProtocolSupport;
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

    // ========================= 分片上传 =========================

    /**
     * 若平台兼容 S3 协议且未实现自有分片封装，可返回 S3 协议支持对象用于降级分片；否则返回 null。
     * <p>
     * 已实现自有分片的平台（如 MinIO）应重写分片方法，不必提供本对象。
     *
     * @return S3 协议分片支持，可为 null
     */
    default S3ProtocolSupport s3ProtocolSupport() {
        return null;
    }

    /**
     * 是否支持分片上传。
     * <p>
     * 默认：存在 S3 协议降级则视为支持；已实现自有分片的平台请重写为 true。
     */
    default boolean supportMultipartUpload() {
        return s3ProtocolSupport() != null;
    }

    /**
     * 初始化分片上传。
     * <p>
     * 默认走 S3 协议降级；平台有自有封装时请重写。
     */
    default MultipartInitResult initiateMultipartUpload(FileStorageInfo fileStorageInfo) {
        S3ProtocolSupport s3 = s3ProtocolSupport();
        if (s3 != null) {
            return s3.initiateMultipartUpload(fileStorageInfo);
        }
        throw unsupportedMultipart();
    }

    /**
     * 上传单个分片。
     * <p>
     * 默认走 S3 协议降级；平台有自有封装时请重写。
     */
    default MultipartPartETag uploadPart(FileStorageInfo fileStorageInfo, String uploadId, int partNumber,
        InputStream partData, long partSize) {
        S3ProtocolSupport s3 = s3ProtocolSupport();
        if (s3 != null) {
            return s3.uploadPart(fileStorageInfo, uploadId, partNumber, partData, partSize);
        }
        throw unsupportedMultipart();
    }

    /**
     * 合并分片完成上传。
     * <p>
     * 默认走 S3 协议降级；平台有自有封装时请重写。
     */
    default boolean completeMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId,
        List<MultipartPartETag> parts) {
        S3ProtocolSupport s3 = s3ProtocolSupport();
        if (s3 != null) {
            return s3.completeMultipartUpload(fileStorageInfo, uploadId, parts);
        }
        throw unsupportedMultipart();
    }

    /**
     * 取消分片上传。
     * <p>
     * 默认走 S3 协议降级；平台有自有封装时请重写。
     */
    default boolean abortMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId) {
        S3ProtocolSupport s3 = s3ProtocolSupport();
        if (s3 != null) {
            return s3.abortMultipartUpload(fileStorageInfo, uploadId);
        }
        throw unsupportedMultipart();
    }

    /** 当前平台不支持分片时的统一异常 */
    default GlobalException unsupportedMultipart() {
        return new GlobalException("【文件服务】平台【" + getPlatform() + "】不支持分片上传（无自有封装且未启用 S3 协议降级）");
    }
}
