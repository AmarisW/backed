package com.gqtian.redis.interceptor;

import static com.gqtian.redis.constant.SystemConstant.REDIS_KEY.TOKEN_BUCKET_LIMIT;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.redis.annotation.RequestLimit;
import com.gqtian.redis.utils.RedisCommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 令牌桶限流拦截器
 *
 * 基于Redis实现的令牌桶限流算法，通过@RequestLimit注解标记需要限流的接口。
 * 相比固定窗口和滑动窗口，令牌桶能够支持突发流量，同时保证长期的平均流量控制。
 *
 * 核心优势：
 * 1. 支持突发：令牌可以积累，允许短时间内突发请求
 * 2. 流畅：通过持续生成令牌实现平滑的速率控制
 * 3. 灵活：支持毫秒到天级的任意时间粒度
 *
 * 工作流程：
 * 1. 拦截请求，检查是否有@RequestLimit注解
 * 2. 从请求参数中获取token作为限流标识
 * 3. 调用checkTokenBucketLimit进行令牌桶限流检查
 * 4. 若令牌不足则拒绝请求并抛出异常，否则消耗令牌并允许请求继续处理
 *
 * 数据存储结构：
 * - Redis Key: "token_bucket_limit:{token}"
 * - 数据类型: String (Redis Sorted Set - 用于存储令牌数量和上次补充时间)
 * - 存储格式: tokens:lastRefillTime (以冒号分隔)
 * - tokens: 当前可用令牌数
 * - lastRefillTime: 上次补充令牌的时间戳(毫秒)
 *
 * @className TokenBucketLimitInterceptor
 * @author gqtian
 * @date 2025/12/18 11:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBucketLimitInterceptor implements HandlerInterceptor {

    // Redis模板，用于操作令牌桶中的令牌数据
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 前置请求拦截处理
     *
     * 在请求到达Controller前进行令牌桶限流检查。如果方法有@RequestLimit注解，则执行令牌桶限流验证。
     * 若请求被限流拒绝，直接抛出GlobalException，不会到达业务处理器。
     *
     * @param request HTTP请求对象，用于获取请求参数和token
     * @param response HTTP响应对象，用于返回错误信息
     * @param handler 请求处理器（Controller方法）
     * @return true 表示请求通过限流检查，继续处理；false 表示请求被中断（本实现中异常时返回true)
     * @throws Exception 当token为空或令牌不足时抛出GlobalException
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        // 检查handler是否是HandlerMethod（即Controller中的方法）
        if (handler instanceof HandlerMethod handlerMethod) {
            // 检查方法是否有@RequestLimit注解，只有标记该注解的方法才需要限流检查
            if (handlerMethod.hasMethodAnnotation(RequestLimit.class)) {
                // 获取@RequestLimit注解的配置信息
                RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);

                // 获取token（限流的唯一标识，通常为用户ID或IP地址）
                String token = StringUtils.EMPTY;

                // region Token获取方式 - 可根据实际需求调整
                // 方式1：从请求参数中获取（推荐用于一般接口）
                token = request.getParameter(requestLimit.name());

                // 方式2：从请求头中获取（推荐用于API接口或移动端）
                // token = request.getHeader(requestLimit.name());
                // endregion

                // token 验证
                if (StringUtils.isEmpty(token)) {
                    throw new GlobalException("【令牌桶限流】请求标识为空");
                }

                // 执行限流检查
                if (checkTokenBucketLimit(token, requestLimit)) {
                    // 请求通过限流检查，记录日志并允许继续处理
                    log.info("【令牌桶限流】请求检查通过，token: {}", token);
                    return true;
                } else {
                    // 请求被限流拒绝，记录警告日志
                    log.warn("【令牌桶限流】请求被拒绝（令牌不足），token: {}，限流设置: {}个令牌/{}{}", token,
                        requestLimit.limitTimes(), requestLimit.timeout(),
                        RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                    // 抛出异常，返回给客户端限流拒绝的提示信息
                    throw new GlobalException(
                        "【令牌桶限流】请求被拒绝，token: " + token + "，限流设置: " + requestLimit.limitTimes() + "个令牌/"
                            + requestLimit.timeout() + RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                }
            }
        }
        return true;
    }

    /**
     * 检查令牌桶限流
     * 时间复杂度：
     * 内存占用：约50字节/token标识 (自动过期，无内存泄漏风险)
     *
     * @param token 限流标识（通常为用户ID或IP地址）
     * @param limit @RequestLimit注解配置，包含limitTimes(桶容量)、timeout、timeUnit
     * @return true 表示请求通过限流检查（令牌足够）；false 表示令牌不足（请求被拒绝）
     */
    public boolean checkTokenBucketLimit(String token, RequestLimit limit) {
        // 生成Redis Key，用于存储该token对应的令牌桶状态 (暂时不加Token 所有请求公用一个桶)
        // String key = TOKEN_BUCKET_LIMIT + token;
        String key = TOKEN_BUCKET_LIMIT + "all";
        long now = System.currentTimeMillis();
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        try {
            // 1. 删除过期的令牌
            long period = RedisCommonUtil.convertToMillis(limit.timeout(), limit.timeUnit());
            zSetOperations.removeRangeByScore(key, 0, now - period);

            // 2. 统计当前令牌数
            Long count = zSetOperations.zCard(key);
            if (count != null && count < limit.limitTimes()) {
                // 3. 新增令牌（分数为当前时间戳）
                zSetOperations.add(key, now, now);
                // 4. 设置键过期时间（避免垃圾数据）
                redisTemplate.expire(key, period, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception e) {
            // 异常处理：Redis操作失败时
            log.error("【令牌桶限流】检查限流异常，token: {}", token, e);
            // 策略：异常时放行请求，避免限流成为新的系统瓶颈
            return true;
        }
    }

}
