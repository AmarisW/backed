/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 平台配置类
 *
 * @author recruitment
 */
@Data
@Component
@ConfigurationProperties(prefix = "platform")
public class PlatformProperties {

    /**
     * 请求地址前缀
     */
    private String requestUrlPrefix;

    /**
     * 用户信息（个体）
     */
    private String userInfoObject;

    /**
     * 用户信息（分页）
     */
    private String userInfoPage;
}
