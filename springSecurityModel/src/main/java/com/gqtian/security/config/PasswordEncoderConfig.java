package com.gqtian.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 🔔 密码编码器配置
 *
 * <p>独立配置类，避免与 {@link SecurityConfig} 产生循环依赖：
 * SecurityConfig 需要 UserDetailsService，而 UserDetailsService 初始化时又需要 PasswordEncoder。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Configuration
public class PasswordEncoderConfig {

  /**
   * BCrypt 单向哈希，同一明文每次加密结果不同（内置盐值），适合存储用户密码
   *
   * @return PasswordEncoder 实例
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
