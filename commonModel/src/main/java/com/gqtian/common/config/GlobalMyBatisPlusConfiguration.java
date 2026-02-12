package com.gqtian.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Plus 全局配置类
 * <p>
 * 配置 MyBatis-Plus 相关功能，并扫描 commonModel 包下的 Mapper 接口
 * 使其能够被 Spring 容器管理和注入
 * </p>
 * <p>
 * 该配置类由 GlobalDatabaseAutoConfiguration 管理，会自动根据条件启用
 * </p>
 *
 * @author gqtian
 * @date 2025-06-02 14:39:44
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.gqtian.common.mapper") // 扫描commonModel包下的Mapper接口
public class GlobalMyBatisPlusConfiguration {


}
