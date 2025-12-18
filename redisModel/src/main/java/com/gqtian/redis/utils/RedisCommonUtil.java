package com.gqtian.redis.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 🤨 Redis相关的通用工具类
 * 
 * 提供Redis操作中常用的工具方法，包括时间单位描述转换和客户端IP地址提取等功能。
 * 这些工具方法被AOP、Service等各个层级的代码广泛使用，以减少代码重复，提高代码复用率。
 * 
 * @className RedisCommonUtil
 * @author gqtian
 * @date 2025/12/18 15:11
 */
@Component
public class RedisCommonUtil {

    /**
     * 将TimeUnit时间单位转换为中文描述
     * 
     * 此方法用于在日志、异常信息或UI展示中，将Java的TimeUnit枚举值转换为用户易读的中文描述。
     * 方便理解系统中使用的时间单位。
     * 
     * 支持的时间单位及对应的中文描述：
     * - MILLISECONDS -> 毫秒
     * - SECONDS -> 秒
     * - MINUTES -> 分钟
     * - HOURS -> 小时
     * - DAYS -> 天
     * 
     * @param timeUnit 待转换的TimeUnit对象，可以为null
     * @return 对应的中文描述字符串；如果timeUnit不在已知范围内，返回"未知"
     */
    public static String timeUnitDesc(TimeUnit timeUnit) {
        switch (timeUnit) {
            case MILLISECONDS:
                return "毫秒";
            case SECONDS:
                return "秒";
            case MINUTES:
                return "分钟";
            case HOURS:
                return "小时";
            case DAYS:
                return "天";
            default:
                return "未知";
        }
    }

    /**
     * 从HTTP请求中智能提取客户端真实IP地址
     *
     * 此方法用于从HTTP请求中获取客户端的真实IP地址。在分布式系统中，请求可能经过多个代理层
     * （如Nginx反向代理、负载均衡器、CDN等），直接调用request.getRemoteAddr()只能获得最后
     * 一级代理的IP。本方法通过检查多个HTTP请求头，按优先级顺序尝试获取原始客户端IP。
     *
     * 检查的请求头优先级顺序：
     * 1. X-Forwarded-For：标准HTTP头，记录请求经过的所有代理IP
     *    格式为 "client_ip, proxy1_ip, proxy2_ip, ..."，本方法取第一个IP（最原始的客户端IP）
     * 2. X-Real-IP：由nginx等反向代理设置，记录客户端真实IP
     * 3. Proxy-Client-IP：某些代理服务器设置的请求头
     * 4. WL-Proxy-Client-IP：WebLogic服务器和代理设置的请求头
     * 5. HTTP_CLIENT_IP：HTTP协议中的客户端IP请求头
     * 6. HTTP_X_FORWARDED_FOR：HTTP协议版本的X-Forwarded-For请求头
     * 7. request.getRemoteAddr()：兜底方案，直接获取TCP连接的远程地址
     *
     * 处理细节：
     * - 对于X-Forwarded-For头包含多个IP的情况（以逗号分隔），取第一个IP
     * - 过滤掉"unknown"值（某些代理返回的默认值）
     * - 对每个候选值进行null和empty检查
     * - 获取到有效IP后立即返回，不再检查后续请求头
     *
     * 使用示例：
     * ```java
     * // 在Controller方法中获取客户端IP
     * @PostMapping("/api/test")
     * public void test(HttpServletRequest request) {
     *     String clientIp = RedisCommonUtil.findClientIp(request);
     *     log.info("请求来自IP：{}", clientIp);
     * }
     * ```
     *
     * @param request HTTP请求对象，由Spring框架自动注入提供
     * @return 客户端IP地址字符串。如果所有检查都失败，返回request.getRemoteAddr()的结果
     */
    public static String findClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        // 兜底方案：直接获取远程地址
        return request.getRemoteAddr();
    }
}
