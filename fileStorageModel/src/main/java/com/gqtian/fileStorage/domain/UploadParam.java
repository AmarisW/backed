package com.gqtian.fileStorage.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 👻 上传对象
 * 
 * @className UploadParam
 * @author gqtian
 * @date 2025/1/16 17:34
 */
@Data
public class UploadParam {

    private String platform;

    private MultipartFile file;

}
