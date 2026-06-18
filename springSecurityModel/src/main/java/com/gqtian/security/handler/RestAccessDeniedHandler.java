package com.gqtian.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.gqtian.common.globalProcess.GlobalResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 🔔 无权限访问处理器（403）
 *
 * <p>触发场景：用户已登录（JWT 有效），但不满足 URL 规则或 {@code @PreAuthorize} 方法权限。
 * 与 401 的区别：401 表示"不知道你是谁"，403 表示"知道你是谁但没有权限"。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * @param request               当前请求
   * @param response              响应对象
   * @param accessDeniedException 权限不足异常
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(JSON.toJSONString(GlobalResult.failure("403", "无权限访问")));
  }
}
