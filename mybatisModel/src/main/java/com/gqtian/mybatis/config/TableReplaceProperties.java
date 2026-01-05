/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.mybatis.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 🔔 表替换配置文件
 *
 * @author GQTIAN
 * @date 2025/6/6 16:33
 */
@Data
@Component
@ConfigurationProperties(prefix = "table-replace")
public class TableReplaceProperties {

    // 是否开启表名替换
    private boolean enabled;

    // 替换表名配置 - 通过键值对替换
    private Map<String, String> tableNameMap;
}
