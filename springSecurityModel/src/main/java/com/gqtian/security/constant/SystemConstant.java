package com.gqtian.security.constant;

/**
 * 🔔 全局静态变量
 *
 * <p>Spring Security 要求角色权限以 {@code ROLE_} 为前缀，
 * 在 {@code hasRole("ADMIN")} 表达式中写 ADMIN 即可，框架会自动匹配 ROLE_ADMIN。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
public class SystemConstant {

  private SystemConstant() {}

  /** 管理员角色（完整权限标识） */
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  /** 普通用户角色（完整权限标识） */
  public static final String ROLE_USER = "ROLE_USER";
}
