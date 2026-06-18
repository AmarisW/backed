package com.gqtian.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.security.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 👤 普通用户接口示例
 *
 * <p>演示 USER / ADMIN 均可访问的用户域接口。
 * URL 规则：{@code /user/**} 需要 USER 或 ADMIN 角色。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

  /**
   * 用户个人资料
   *
   * <p>权限由 URL 规则保障，admin 和 user 账号均可访问。</p>
   *
   * @return 个人资料描述
   */
  @GetMapping("/profile")
  public String profile() {
    return "用户 " + SecurityUtils.getCurrentUsername() + " 的个人资料";
  }

  /**
   * 用户订单列表（演示方法级权限）
   *
   * <p>{@code hasAnyRole('USER', 'ADMIN')} 表示两个角色任一满足即可访问。</p>
   *
   * @return 订单列表描述
   */
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @GetMapping("/orders")
  public String orders() {
    return SecurityUtils.getCurrentUsername() + " 的订单列表（演示）";
  }
}
