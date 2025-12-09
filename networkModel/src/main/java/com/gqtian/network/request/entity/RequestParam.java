/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.request.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTTP请求参数
 *
 * @author gqtian
 * @date: 2025/10/21 16:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestParam {

    /**
     * 页码
     */
    @JSONField(name = "current")
    private int pageNo;

    /**
     * 每页数量
     */
    @JSONField(name = "size")
    private int pageSize;

}
