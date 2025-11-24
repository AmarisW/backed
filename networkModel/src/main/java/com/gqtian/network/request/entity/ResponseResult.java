/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.request.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🤨 通用返回值
 *
 * @className ResponDto
 * @author gqtian
 * @date: 2025/10/21 15:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> {

    /**
     *   success
     */
    private boolean success;

    /**
     *  编码
     */
    private String code;

    /**
     *  消息
     */
    private String msg;

    /**
     *   数据
     */
    private T data;

    /**
     *  时间戳
     */
    private String timestamp;

}
