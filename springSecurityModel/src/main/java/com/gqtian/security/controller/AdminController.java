package com.gqtian.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.security.constant.RoleConstant;
import com.gqtian.security.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 👑 管理员接口示例
 *
 * <p>演示两层权限控制：</p>
 * <ul>
 *   <li><b>URL 级</b>：{@link com.gqtian.security.config.SecurityConfig} 中配置 {@code /admin/**} 需 ADMIN 角色</li>
 *   <li><b>方法级</b>：{@code @PreAuthorize} 对单个接口做更细粒度限制</li>
 * </ul>
 *
 * <p>演示账号：admin 可访问；user 访问将返回 403。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

  /**
   * 管理后台首页
   *
   * <p>权限由 URL 规则保障：{@code /admin/**} → {@code hasRole('ADMIN')}</p>
   *
   * @return 欢迎信息
   */
  @GetMapping("/dashboard")
  public String dashboard() {
    return "欢迎管理员 " + SecurityUtils.getCurrentUsername() + "，这是管理后台";
  }

  /**
   * 删除用户（演示方法级权限）
   *
   * <p>在 URL 规则基础上叠加 {@code @PreAuthorize}，仅 ADMIN 可调用。
   * 实际项目中可用于同一前缀下区分读写权限。</p>
   *
   * @return 操作结果描述
   */
  @PreAuthorize("hasRole('" + RoleConstant.ADMIN + "')")
  @GetMapping("/delete-user")
  public String deleteUser() {
    log.info("管理员 {} 执行了删除用户操作", SecurityUtils.getCurrentUsername());
    return "删除用户操作已记录（演示）";
  }
}
