/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.entity;

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
public class CollectResult<T> {

    /**
     *   是否成功
     */
    private Integer status;

    /**
     *  错误码
     */
    private String errorCode;

    /**
     *  错误消息
     */
    private String errorMsg;

    /**
     *   返回数据
     */
    private T data;

}
