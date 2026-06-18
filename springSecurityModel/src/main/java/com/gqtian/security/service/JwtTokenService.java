package com.gqtian.security.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.gqtian.security.config.JwtProperties;
import com.gqtian.security.model.SecurityUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

/**
 * 🔔 JWT Token 生成与解析服务
 *
 * <p>基于 jjwt 库，使用 HMAC-SHA 对称签名。Token Payload 包含：</p>
 * <ul>
 *   <li>{@code sub} — 用户名</li>
 *   <li>{@code userId} — 用户 ID</li>
 *   <li>{@code roles} — 角色列表（如 ROLE_ADMIN）</li>
 *   <li>{@code iat} / {@code exp} — 签发时间与过期时间</li>
 * </ul>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {

  /** JWT 配置（密钥、过期时间等） */
  private final JwtProperties jwtProperties;

  /**
   * 根据已认证用户生成 JWT
   *
   * @param user 登录成功的用户详情
   * @return 签名后的 Token 字符串
   */
  public String generateToken(SecurityUser user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());
    List<String> roles = user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return Jwts.builder()
        .subject(user.getUsername())
        .claim("userId", user.getId())
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey())
        .compact();
  }

  /**
   * 解析并验签 Token，失败时抛出 jwt 异常
   *
   * @param token JWT 字符串
   * @return Claims 载荷
   */
  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(secretKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * 从 Token 中提取用户名（对应 sub 字段）
   *
   * @param token JWT 字符串
   * @return 用户名
   */
  public String extractUsername(String token) {
    return parseToken(token).getSubject();
  }

  /**
   * 从 Token 中提取角色列表
   *
   * @param token JWT 字符串
   * @return 角色名称列表，如 ["ROLE_ADMIN", "ROLE_USER"]
   */
  @SuppressWarnings("unchecked")
  public List<String> extractRoles(String token) {
    return parseToken(token).get("roles", List.class);
  }

  /**
   * 根据配置的 secret 生成 HMAC 签名密钥
   */
  private SecretKey secretKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
