package com.gqtian.easytrans.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 🤨 Redis 配置类
 * <p>
 * 配置 StringRedisTemplate，支持 Easy-Trans 字典数据存储到 Redis
 * </p>
 *
 * @className RedisConfig
 * @author gqtian
 * @date 2026/6/23
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 StringRedisTemplate
     * <p>
     * Easy-Trans Redis 模块需要 StringRedisTemplate
     * </p>
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
