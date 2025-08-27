package com.gqtian.contrast.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 👻 mybatis配置
 * 
 * @className mybatisConfig
 * @author gqtian
 * @date 2025/8/15 00:08
 */
@Configuration
@MapperScan("com.gqtian.**.mapper")
public class MybatisConfig {

}
