package com.gqtian.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gqtian.security.config.JwtProperties;
import com.gqtian.security.service.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🔔 JWT 认证过滤器
 *
 * <p>每个请求执行一次（继承 {@link OncePerRequestFilter}），职责：</p>
 * <ol>
 *   <li>从请求头解析 Bearer Token</li>
 *   <li>验签并提取用户名、角色列表</li>
 *   <li>构造 {@link UsernamePasswordAuthenticationToken} 写入 {@link SecurityContextHolder}</li>
 * </ol>
 *
 * <p>注意：Token 无效时仅记录警告并放行，最终由 Security 授权规则返回 401，
 * 避免在过滤器层直接中断响应，保持与 Spring Security 异常处理一致。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 * @see com.gqtian.security.config.SecurityConfig
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /** JWT 相关配置（Header 名、前缀等） */
  private final JwtProperties jwtProperties;

  /** Token 解析服务 */
  private final JwtTokenService jwtTokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = resolveToken(request);
    // 仅当携带 Token 且当前上下文尚未认证时才尝试解析，避免重复覆盖
    if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        String username = jwtTokenService.extractUsername(token);
        List<String> roles = jwtTokenService.extractRoles(token);
        List<SimpleGrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        // 凭证已体现在 JWT 中，此处 principal 为用户名，credentials 置 null
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ex) {
        // 过期、签名错误、格式非法等均在此捕获，交由后续授权判断返回 401
        log.warn("JWT 解析失败: {}", ex.getMessage());
      }
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 从请求头提取纯 Token 字符串（去掉 Bearer 前缀）
   *
   * @param request HTTP 请求
   * @return Token 字符串；未携带或格式不符时返回 null
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(jwtProperties.getHeader());
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getPrefix() + " ")) {
      return bearerToken.substring(jwtProperties.getPrefix().length() + 1);
    }
    return null;
  }
}
