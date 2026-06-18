package com.gqtian.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security 上下文工具类
 *
 * <p>从 {@link SecurityContextHolder} 读取当前请求的认证信息，
 * 供 Controller / Service 层便捷获取登录用户，无需手动解析 JWT。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
public class SecurityUtils {

  private SecurityUtils() {}

  /**
   * 获取当前登录用户名
   *
   * @return 用户名；未认证时返回 null
   */
  public static String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    return authentication.getName();
  }

  /**
   * 判断当前用户是否拥有指定角色
   *
   * @param role 角色名，可带或不带 ROLE_ 前缀
   * @return 拥有该角色返回 true
   */
  public static boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    // hasRole 表达式会自动补 ROLE_ 前缀，工具方法与之保持一致
    String targetRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(targetRole::equals);
  }
}
