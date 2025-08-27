/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.enums;

import lombok.Getter;

/**
 * 👻 平台枚举
 *
 * @className PlatformEnum
 * @author gqtian
 * @date 2025/1/13 15:10
 */
@Getter
public enum PlatformEnum {

    /** 阿里云 */
    ALIYUN_OSS("aliYunOss", "阿里云OSS", "com.aliyun.oss.OSS"),

    /** 华为云 */
    HUAWEI_OBS("huaweiObs", "华为云OBS", "com.obs.services.ObsClient"),

    /** FtpOss */
    FTP_OSS("ftp", "ftp", "org.apache.commons.net.ftp.FTPClient"),

    /** minio */
    MINIO("minio", "minio", "io.minio.MinioClient");

    private final String code;

    private final String name;

    private final String classPath;

    PlatformEnum(String code, String name, String classPath) {
        this.code = code;
        this.name = name;
        this.classPath = classPath;
    }
}
