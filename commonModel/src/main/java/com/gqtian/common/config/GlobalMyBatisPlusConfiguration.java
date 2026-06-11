package com.gqtian.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.gqtian.common.interceptor.GlobalSqlPrintInterceptor;

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
@AutoConfiguration
@MapperScan(basePackages = "com.gqtian.**.mapper") // 扫描commonModel包下的Mapper接口
public class GlobalMyBatisPlusConfiguration {

    /**
     * 注册SQL打印拦截器
     *
     * @return SQL打印拦截器实例
     */
    @Bean
    @ConditionalOnClass(SqlSessionFactory.class) // 仅在类路径下存在 SqlSessionFactory 时注册拦截器
    public GlobalSqlPrintInterceptor globalSqlPrintInterceptor() {
        return new GlobalSqlPrintInterceptor();
    }

}
