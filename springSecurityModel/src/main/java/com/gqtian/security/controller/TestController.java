package com.gqtian.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🧪 公开 / 受保护接口对比示例
 *
 * <p>用于快速验证 Spring Security 配置是否生效：</p>
 * <ul>
 *   <li>{@code /test/public/**} — 匿名可访问（白名单）</li>
 *   <li>{@code /test/protected/**} — 需有效 JWT（走默认 authenticated 规则）</li>
 * </ul>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@RestController
@RequestMapping("/test")
public class TestController {

  /**
   * 公开接口，无需登录
   *
   * @return 问候语
   */
  @GetMapping("/public/hello")
  public String publicHello() {
    return "Hello, Spring Security! 这是公开接口";
  }

  /**
   * 受保护接口，需在 Header 携带 {@code Authorization: Bearer <token>}
   *
   * @return 问候语
   */
  @GetMapping("/protected/hello")
  public String protectedHello() {
    return "Hello, 你已登录，可以访问受保护资源";
  }
}
