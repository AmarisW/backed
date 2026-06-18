package com.gqtian.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 🔔 JWT 配置属性
 *
 * <p>绑定 {@code application-base.yml} 中 {@code security.jwt} 节点，
 * 生产环境务必更换 {@link #secret}，且长度满足 HMAC-SHA 算法要求（建议 ≥ 32 字符）。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Data
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

  /** 签名密钥，用于 Token 签发与验签，泄露将导致任意伪造 Token */
  private String secret;

  /** Token 有效期（毫秒），默认 24 小时 */
  private long expiration;

  /** 请求头名称，通常为 Authorization */
  private String header;

  /** Token 前缀，与 Token 值之间需有一个空格，如 {@code Bearer eyJhbG...} */
  private String prefix;
}
