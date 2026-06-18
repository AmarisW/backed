package com.gqtian.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security 模块启动类
 *
 * <p>认证授权整体流程：</p>
 * <ol>
 *   <li>客户端调用 {@code /auth/login} 提交用户名密码</li>
 *   <li>{@link org.springframework.security.authentication.AuthenticationManager} 校验凭证</li>
 *   <li>校验通过后由 {@link com.gqtian.security.service.JwtTokenService} 签发 JWT</li>
 *   <li>后续请求在 Header 携带 {@code Authorization: Bearer <token>}</li>
 *   <li>{@link com.gqtian.security.filter.JwtAuthenticationFilter} 解析 Token 并写入 SecurityContext</li>
 *   <li>URL 规则（{@link com.gqtian.security.config.SecurityConfig}）与方法注解（{@code @PreAuthorize}）共同完成鉴权</li>
 * </ol>
 *
 * @author GQTIAN
 * @date 2026/6/18
 * @see com.gqtian.security.config.SecurityConfig
 */
@Slf4j
@EnableCaching
@EnableMethodSecurity // 开启方法级权限，支持 @PreAuthorize、@PostAuthorize 等注解
@SpringBootApplication(exclude = {GlobalDatabaseAutoConfiguration.class}) // 本模块使用内存用户，排除数据库自动配置
public class SpringSecurityModelApplication {

  public static void main(String[] args) {
    GlobalBeautifyStart.start(SpringSecurityModelApplication.class, args);
  }
}
