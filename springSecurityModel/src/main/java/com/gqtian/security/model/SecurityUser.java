package com.gqtian.security.model;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

/**
 * 自定义用户详情实体
 *
 * <p>实现 {@link UserDetails} 接口，作为 Spring Security 认证体系中的用户模型。
 * 登录成功后会被放入 {@link org.springframework.security.core.Authentication#getPrincipal()}。</p>
 *
 * <p>角色命名须遵循 Spring Security 约定：存储时带 {@code ROLE_} 前缀，
 * 以便 {@code hasRole('ADMIN')} 等表达式正确匹配。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Getter
public class SecurityUser implements UserDetails {

  /** 用户唯一标识，写入 JWT 的 userId 字段 */
  private final Long id;

  /** 登录用户名 */
  private final String username;

  /** BCrypt 加密后的密码，仅供 AuthenticationManager 内部比对 */
  private final String password;

  /** 用户权限集合，由角色字符串转换而来 */
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * @param id       用户 ID
   * @param username 用户名
   * @param password 加密后的密码
   * @param roles    角色列表，如 ["ROLE_ADMIN", "ROLE_USER"]
   */
  public SecurityUser(Long id, String username, String password, Collection<String> roles) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  /** 账户是否未过期，演示模块默认 true */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /** 账户是否未锁定，演示模块默认 true */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /** 凭证是否未过期，演示模块默认 true */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /** 账户是否启用，演示模块默认 true */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
