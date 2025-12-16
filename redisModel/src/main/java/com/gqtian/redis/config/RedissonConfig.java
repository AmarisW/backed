package com.gqtian.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 🤨 Redisson配置
 * @className RedissonConfig
 * @author gqtian
 * @date 2025/12/16 15:41
 */
@Configuration
public class RedissonConfig {

  /**
   * Redis地址
   */
  @Value(value = "${spring.data.redis.host}")
  private String host;

  /**
   * Redis端口
   */
  @Value(value = "${spring.data.redis.port}")
  private Integer port;

  /**
   * Redis密码
   */
  @Value(value = "${spring.data.redis.password}")
  private String password;

  /**
   * Redis数据库索引
   */
  @Value(value = "${spring.data.redis.database}")
  private Integer database;


  /**
   * 🤨 创建Redisson客户端
   * @return Redisson客户端
   */
  @Bean
  RedissonClient redissonClient() {
    Config config = new Config();
    SingleServerConfig singleServerConfig = config.useSingleServer();
    String address = "redis://" + host + ":" + port;
    singleServerConfig.setAddress(address);
    singleServerConfig.setDatabase(database);
    singleServerConfig.setPassword(password);
    return Redisson.create(config);
  }
}
