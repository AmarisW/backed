/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.request.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTTP请求头
 *
 * @author gqtian
 * @date: 2025/10/21 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestHead {

    /** 访问令牌 */
    @JSONField(name = "Authorization")
    private String authorization;
}
