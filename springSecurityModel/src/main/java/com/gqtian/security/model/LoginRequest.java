package com.gqtian.security.model;

import lombok.Data;

/**
 * 登录请求体
 *
 * <p>对应 {@code POST /auth/login} 的 JSON 入参</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Data
public class LoginRequest {

  /** 登录用户名 */
  private String username;

  /** 登录密码（明文传输，生产环境应使用 HTTPS） */
  private String password;
}
