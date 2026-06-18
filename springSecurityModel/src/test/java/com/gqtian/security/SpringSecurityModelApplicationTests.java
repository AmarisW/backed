package com.gqtian.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 模块启动上下文加载测试
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@SpringBootTest
public class SpringSecurityModelApplicationTests {

  /** 验证 Spring 容器能正常启动，Security 相关 Bean 无循环依赖 */
  @Test
  public void contextLoads() {}

}
