package com.gqtian.security.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.annotation.GlobalNoWrap;
import com.gqtian.security.model.LoginRequest;
import com.gqtian.security.model.LoginResponse;
import com.gqtian.security.model.SecurityUser;
import com.gqtian.security.service.JwtTokenService;
import com.gqtian.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🔐 认证相关接口
 *
 * <p>提供登录签发 JWT、查询当前用户等能力，是客户端与 Spring Security 交互的入口。</p>
 *
 * <p>登录流程：</p>
 * <ol>
 *   <li>客户端 POST {@code /auth/login} 提交用户名密码</li>
 *   <li>{@link AuthenticationManager} 调用 UserDetailsService 加载用户并比对密码</li>
 *   <li>认证成功后由 {@link JwtTokenService} 签发 Token</li>
 *   <li>客户端将 Token 放入后续请求的 {@code Authorization: Bearer xxx} 头</li>
 * </ol>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  /** Spring Security 认证管理器，负责用户名密码校验 */
  private final AuthenticationManager authenticationManager;

  /** JWT 签发与解析服务 */
  private final JwtTokenService jwtTokenService;

  /**
   * 登录并获取 JWT Token
   *
   * <p>使用 {@link GlobalNoWrap} 跳过全局响应包装，直接返回 {@link LoginResponse}，
   * 便于前端按固定结构读取 token 字段。</p>
   *
   * @param request 登录请求（username + password）
   * @return 包含 token、角色等信息的登录响应；认证失败时由 Spring Security 抛出 401
   */
  @GlobalNoWrap
  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    // 构造未认证令牌，交由 AuthenticationManager 完成校验
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    SecurityUser user = (SecurityUser) authentication.getPrincipal();
    String token = jwtTokenService.generateToken(user);
    List<String> roles = user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    log.info("用户 {} 登录成功", user.getUsername());
    return LoginResponse.builder()
        .token(token)
        .tokenType("Bearer")
        .username(user.getUsername())
        .roles(roles)
        .build();
  }

  /**
   * 获取当前登录用户信息
   *
   * <p>需在请求头携带有效 JWT，由 {@link com.gqtian.security.filter.JwtAuthenticationFilter} 解析后写入上下文。</p>
   *
   * @return 当前用户名描述字符串
   */
  @GetMapping("/me")
  public String currentUser() {
    return "当前登录用户: " + SecurityUtils.getCurrentUsername();
  }
}
