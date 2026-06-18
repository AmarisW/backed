package com.gqtian.security.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.gqtian.common.globalProcess.GlobalResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 🔔 未认证访问处理器（401）
 *
 * <p>触发场景：访问受保护接口时未携带 Token、Token 无效或已过期。
 * 返回 JSON 格式统一响应，避免默认跳转登录页（适用于前后端分离）。</p>
 *
 * @author GQTIAN
 * @date 2026/6/18
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * @param request       当前请求
   * @param response      响应对象
   * @param authException 认证异常（本实现中未透出具体原因，防止信息泄露）
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(JSON.toJSONString(GlobalResult.failure("401", "未登录或 Token 已失效")));
  }
}
