package com.gqtian.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import com.gqtian.common.initialization.GlobalDictInitRun;

/**
 * 数据库相关自动配置类
 */
@AutoConfiguration
@Import({GlobalMyBatisPlusConfiguration.class, GlobalDictInitRun.class})
public class GlobalDatabaseAutoConfiguration {}
