/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 分片上传完成后的分片标识（partNumber + ETag），用于合并分片。
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipartPartETag implements Serializable {

    /** 分片序号，从 1 开始 */
    private int partNumber;

    /** 分片上传返回的 ETag */
    private String etag;
}
