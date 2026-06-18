package com.gqtian.security.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功响应体
 *
 * <p>使用 {@link com.gqtian.common.annotation.GlobalNoWrap} 标记登录接口，
 * 避免被全局响应包装器二次封装，方便客户端直接读取 token 字段。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Data
@Builder
public class LoginResponse {

  /** JWT Token 字符串，客户端后续请求放入 Authorization 头 */
  private String token;

  /** Token 类型，固定为 Bearer */
  private String tokenType;

  /** 登录成功的用户名 */
  private String username;

  /** 用户拥有的角色列表，如 ["ROLE_ADMIN", "ROLE_USER"] */
  private List<String> roles;
}
