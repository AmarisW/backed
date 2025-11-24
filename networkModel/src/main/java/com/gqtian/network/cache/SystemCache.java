/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.cache;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统通用缓存
 *
 * @author gqtian
 * @date: 2025/10/21 14:09
 */
@Slf4j
@Component
public class SystemCache {

    /** 访问令牌 */
    public static String AUTHORIZATION;
}
