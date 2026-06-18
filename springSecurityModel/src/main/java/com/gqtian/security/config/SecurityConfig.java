package com.gqtian.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gqtian.security.constant.SystemConstant;
import com.gqtian.security.filter.JwtAuthenticationFilter;
import com.gqtian.security.handler.RestAccessDeniedHandler;
import com.gqtian.security.handler.RestAuthenticationEntryPoint;
import com.gqtian.security.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 * 🔔 Spring Security 核心配置
 *
 * <p>负责定义认证管理器、安全过滤器链及 URL 访问规则。
 * 本模块采用 JWT 无状态方案，不创建 HttpSession。</p>
 *
 * <p>权限规则优先级（自上而下匹配）：</p>
 * <ul>
 *   <li>{@code /auth/login}、{@code /test/public/**} — 匿名可访问</li>
 *   <li>{@code /admin/**} — 需要 ADMIN 角色</li>
 *   <li>{@code /user/**} — 需要 USER 或 ADMIN 角色</li>
 *   <li>其余接口 — 仅需登录（有效 JWT）</li>
 * </ul>
 *
 * @author GQTIAN
 * @date 2026/6/18
 * @see JwtAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /** 用户数据源，登录时由 AuthenticationManager 调用 */
  private final UserDetailsServiceImpl userDetailsService;

  /** JWT 解析过滤器，在 UsernamePasswordAuthenticationFilter 之前执行 */
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /** 未登录访问受保护资源时的 401 处理器 */
  private final RestAuthenticationEntryPoint authenticationEntryPoint;

  /** 已登录但权限不足时的 403 处理器 */
  private final RestAccessDeniedHandler accessDeniedHandler;

  /**
   * 认证管理器：整合 UserDetailsService + PasswordEncoder 完成用户名密码校验
   *
   * @param passwordEncoder BCrypt 密码编码器
   * @return AuthenticationManager 实例，供登录接口注入使用
   */
  @Bean
  public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }

  /**
   * 安全过滤器链：JWT 无状态认证的核心装配点
   *
   * @param http HttpSecurity 构建器
   * @return 配置完成的安全过滤器链
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // REST API 使用 JWT，关闭 CSRF（若前后端分离 + Cookie 方案需重新评估）
        .csrf(AbstractHttpConfigurer::disable)
        // 无状态：不创建 Session，认证信息完全依赖 JWT
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/login", "/test/public/**").permitAll()
            // hasRole 会自动补全 ROLE_ 前缀，此处传入 "ADMIN" 即匹配 ROLE_ADMIN
            .requestMatchers("/admin/**").hasRole(SystemConstant.ROLE_ADMIN.replace("ROLE_", ""))
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated())
        // JWT 过滤器必须在 UsernamePasswordAuthenticationFilter 之前，优先从 Header 恢复认证上下文
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
