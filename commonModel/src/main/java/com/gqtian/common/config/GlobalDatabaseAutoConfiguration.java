package com.gqtian.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import com.gqtian.common.initialization.GlobalDictInitRun;

/**
 * 数据库相关自动配置类
 * <p>
 * 该配置类仅在以下条件满足时启用：
 * 1. classpath中存在DataSource类（即引入了数据库相关依赖）
 * 2. 配置项 spring.datasource.enabled 不为 false（默认启用）
 * </p>
 * <p>
 * 如果不需要数据库功能，可以通过以下方式禁用：
 * - 方式1：在启动类使用 @SpringBootApplication(exclude = GlobalDatabaseAutoConfiguration.class)
 * </p>
 *
 * @author gqtian
 * @date 2026/02/12
 */
@AutoConfiguration
@Import({GlobalMyBatisPlusConfiguration.class, GlobalDictInitRun.class})
public class GlobalDatabaseAutoConfiguration {}
