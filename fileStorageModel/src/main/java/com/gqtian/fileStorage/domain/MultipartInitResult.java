/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 初始化分片上传的结果，后续 uploadPart / complete / abort 需携带 uploadId。
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipartInitResult implements Serializable {

    /** 存储平台编码 */
    private String platform;

    /** 分片上传会话 ID */
    private String uploadId;

    /** 对象在存储端的完整 key（basePath + path + filename） */
    private String objectKey;

    /** 文件名 */
    private String filename;

    /** 拓展路径 */
    private String path;
}
