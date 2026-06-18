package com.gqtian.security.constant;

/**
 * 🔔 角色常量（用于 @PreAuthorize 表达式）
 *
 * <p>注意：{@code @PreAuthorize("hasRole('ADMIN')")} 中的参数<b>不带</b> ROLE_ 前缀，
 * 与 {@link SystemConstant} 中存储的完整权限标识不同，请勿混用。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 * @see SystemConstant
 */
public class RoleConstant {

  private RoleConstant() {}

  /** 管理员角色（@PreAuthorize 表达式用） */
  public static final String ADMIN = "ADMIN";

  /** 普通用户角色（@PreAuthorize 表达式用） */
  public static final String USER = "USER";
}
