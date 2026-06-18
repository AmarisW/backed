package com.gqtian.security.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gqtian.security.constant.SystemConstant;
import com.gqtian.security.model.SecurityUser;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 🔔 用户详情服务（内存实现）
 *
 * <p>实现 Spring Security 的 {@link UserDetailsService} 接口，
 * 在登录阶段被 {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} 调用，
 * 根据用户名加载用户及其权限信息。</p>
 *
 * <p>当前为演示用的内存用户，生产环境应替换为数据库查询（如对接 MyBatis UserMapper）。</p>
 *
 * <p>演示账号：</p>
 * <ul>
 *   <li>admin / 123456 — ROLE_ADMIN + ROLE_USER</li>
 *   <li>user / 123456 — ROLE_USER</li>
 * </ul>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  /** 密码编码器，初始化演示用户时对明文密码加密 */
  private final PasswordEncoder passwordEncoder;

  /** 内存用户仓库，key 为用户名 */
  private final Map<String, SecurityUser> userStore = new ConcurrentHashMap<>();

  /**
   * 应用启动后初始化演示账号
   * 密码经 BCrypt 加密后存储，与登录时 AuthenticationManager 的比对逻辑一致
   */
  @PostConstruct
  public void initDemoUsers() {
    userStore.put("admin", new SecurityUser(
        10001L,
        "admin",
        passwordEncoder.encode("123456"),
        List.of(SystemConstant.ROLE_ADMIN, SystemConstant.ROLE_USER)));
    userStore.put("user", new SecurityUser(
        10002L,
        "user",
        passwordEncoder.encode("123456"),
        List.of(SystemConstant.ROLE_USER)));
  }

  /**
   * 根据用户名加载用户，登录认证的核心回调
   *
   * @param username 登录用户名
   * @return UserDetails 用户详情（含加密密码与权限）
   * @throws UsernameNotFoundException 用户不存在时抛出，AuthenticationManager 会转换为认证失败
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SecurityUser user = userStore.get(username);
    if (user == null) {
      throw new UsernameNotFoundException("用户不存在: " + username);
    }
    return user;
  }
}
