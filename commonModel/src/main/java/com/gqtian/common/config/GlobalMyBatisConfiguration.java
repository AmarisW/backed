package com.gqtian.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Flex 配置类 配置SQL审计、逻辑删除等功能
 *
 * @author 【全栈学习关注公众号：程序员Mars】
 * @version 1.0
 * @date 2025-06-02 14:39:44
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.gqtian.common.mapper") // 读取commonModel包下的Mapper
public class GlobalMyBatisConfiguration {


}
