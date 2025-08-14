/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.domain;

import java.io.InputStream;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStorageInfo implements Serializable, AutoCloseable {
    
    /** 存储平台 */
    private String platform;
    
    /** 文件标识 */
    private String id;
    
    /** 存储路径 */
    private String path;
    
    /** 文件访问地址 */
    private String url;
    
    /** 文件大小，单位字节 */
    private Long size;
    
    /** 文件名称 */
    private String filename = "";
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /** 原始文件名 */
    private String originalFilename;
    
    /** 文件内容 */
    private Object content;
    
    /** 文件内容 - 文件流 */
    private InputStream inputStream;
    
    @Override
    public void close()
        throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
