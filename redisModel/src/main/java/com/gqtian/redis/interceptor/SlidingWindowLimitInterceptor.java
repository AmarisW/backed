package com.gqtian.redis.interceptor;

import static com.gqtian.redis.constant.SystemConstant.REDIS_KEY.SLIDING_WINDOW_LIMIT;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
 * 滑动窗口限流拦截器
 *
 * 实现基于Redis的滑动窗口限流，通过@RequestLimit注解标记需要限流的接口。
 * 相比固定窗口限流，滑动窗口能够更精确地控制请求速率，避免边界问题。
 *
 * 核心优势：
 * 1. 精确限流：完全按照时间窗口计算，避免固定窗口的边界突增问题
 * 2. 公平性：每个请求都有精确的时间戳记录，确保用户体验一致
 * 3. 灵活性：支持毫秒到天级的任意时间粒度
 *
 * 工作流程：
 * 1. 拦截请求，检查是否有@RequestLimit注解
 * 2. 从请求参数中获取token作为限流标识
 * 3. 调用checkSlidingWindowLimit进行滑动窗口限流检查
 * 4. 若超过限制则拒绝请求并抛出异常，否则允许请求继续处理
 *
 * 数据存储结构：
 * - Redis Key: "sliding_window_limit:{token}"
 * - 数据类型: ZSet (有序集合)
 * - Member: token_nanoTime (唯一标识)
 * - Score: 请求时间戳(毫秒) (用于范围查询和排序)
 *
 * @className SlidingWindowLimitInterceptor
 * @author gqtian
 * @date 2025/12/18 11:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlidingWindowLimitInterceptor implements HandlerInterceptor {

    // Redis操作模板，用于访问Redis ZSet数据结构
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 前置请求拦截处理
     *
     * 在请求到达Controller前进行限流检查。如果方法有@RequestLimit注解，则执行滑动窗口限流验证。
     * 若请求被限流拒绝，直接抛出GlobalException，不会到达业务处理器。
     *
     * @param request HTTP请求对象，用于获取请求参数和token
     * @param response HTTP响应对象，用于返回错误信息
     * @param handler 请求处理器（Controller方法）
     * @return true 表示请求通过限流检查，继续处理；false 表示请求被中断（本实现中异常时返回true)
     * @throws Exception 当token为空或限流拒绝时抛出GlobalException
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
                    throw new GlobalException("【滑动窗口限流】请求标识为空");
                }

                // 执行限流检查
                if (checkSlidingWindowLimit(token, requestLimit)) {
                    // 请求通过限流检查，记录日志并允许继续处理
                    log.info("【滑动窗口限流】请求检查通过，token: {}", token);
                    return true;
                } else {
                    // 请求被限流拒绝，记录警告日志
                    log.warn("【滑动窗口限流】请求被拒绝，token: {}，限流设置: {}次/{}{}", token, requestLimit.limitTimes(),
                        requestLimit.timeout(), RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                    // 抛出异常，返回给客户端限流拒绝的提示信息
                    throw new GlobalException(
                        "【滑动窗口限流】请求被拒绝，token: " + token + "，限流设置: " + requestLimit.limitTimes() + "次/"
                            + requestLimit.timeout() + RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                }
            }
        }
        return true;
    }

    /**
     * 检查滑动窗口限流
     *
     * 核心算法（共4步）：
     * 1. 删除窗口外的请求记录：删除所有score小于windowStartTime的ZSet元素
     * 2. 统计窗口内的请求数：获取ZSet的元素个数
     * 3. 判断是否超过限制：currentCount >= limitTimes时拒绝
     * 4. 记录当前请求：将当前请求的时间戳添加到ZSet中
     *
     * 时间复杂度：
     * - removeRangeByScore: O(log N + M) - N为ZSet大小，M为删除元素数
     * - zCard: O(1) - 常数时间
     * - add: O(log N) - 日志时间
     * - expire: O(1) - 常数时间
     *
     * 内存占用：约100字节/请求 (自动过期，无内存泄漏风险)
     *
     * @param token 限流标识（通常为用户ID或IP地址）
     * @param limit @RequestLimit注解配置，包含limitTimes、timeout、timeUnit
     * @return true 表示请求通过限流检查（未超过限制）；false 表示超过限流次数（请求被拒绝）
     */
    public boolean checkSlidingWindowLimit(String token, RequestLimit limit) {
        // 生成Redis ZSet的key，用于存储该token的所有请求时间戳
        String key = SLIDING_WINDOW_LIMIT + token;

        // 获取当前系统时间戳（毫秒级精度）
        long currentTime = System.currentTimeMillis();

        // 计算滑动窗口的起始时间：currentTime - 窗口大小
        // 例如：当前时间10000ms，窗口大小5000ms，则windowStartTime = 5000ms
        long windowStartTime = currentTime - convertToMillis(limit.timeout(), limit.timeUnit());

        try {
            // ============ 步骤1：清理过期数据 ============
            // 删除ZSet中所有score小于windowStartTime的元素（即窗口外的请求）
            // 这样确保ZSet中只保留最近一个时间窗口内的请求
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStartTime);

            // ============ 步骤2：统计窗口内请求数 ============
            // 获取ZSet的cardinality（元素个数），即当前窗口内的请求总数
            Long requestCount = redisTemplate.opsForZSet().zCard(key);
            long currentCount = requestCount != null ? requestCount : 0;

            // ============ 步骤3：判断是否超过限流阈值 ============
            // 如果当前计数已经达到或超过限流次数，则拒绝该请求
            if (currentCount >= limit.limitTimes()) {
                // 已超过限流阈值，拒绝该请求并记录详细的拒绝原因
                log.warn("【滑动窗口限流】请求被拒绝，token: {}，当前计数: {}，限制: {}次/{}{}，窗口范围: {}ms~{}ms", token, currentCount,
                    limit.limitTimes(), limit.timeout(), RedisCommonUtil.timeUnitDesc(limit.timeUnit()),
                    windowStartTime, currentTime);
                return false;
            }

            // ============ 步骤4：记录当前请求 ============
            // 请求未超过限制，将当前请求的时间戳添加到ZSet中
            // Member格式：token_nanoTime (nanoTime确保唯一性，避免重复)
            // Score：当前时间戳（毫秒），用于后续的范围查询和过期判断）
            redisTemplate.opsForZSet().add(key, token + "_" + System.currentTimeMillis(), currentTime);

            // ============ 步骤5：设置Redis key的过期时间 ============
            // 防止内存泄漏：即使没有新请求，key也会在窗口时长后自动过期
            // 过期时间 = 窗口大小 + 1000ms
            // 额外的1秒是为了确保所有窗口内的数据都被正确删除
            long expireTime = convertToMillis(limit.timeout(), limit.timeUnit()) + 1000;
            redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);

            // 请求成功通过限流检查，记录日志（包含详细的限流窗口信息用于监控）
            log.info("【滑动窗口限流】请求通过，token: {}，当前计数: {}，限制: {}次/{}{}，窗口范围: {}ms~{}ms", token, currentCount + 1,
                limit.limitTimes(), limit.timeout(), RedisCommonUtil.timeUnitDesc(limit.timeUnit()),
                windowStartTime, currentTime);

            return true;
        } catch (Exception e) {
            // 异常处理：Redis操作失败时
            // 记录错误日志，便于后续排查问题
            log.error("【滑动窗口限流】检查限流异常，token: {}", token, e);
            // 策略：异常时放行请求，避免限流成为新的系统瓶颈
            // 这样做的原因：优先保证业务的可用性，限流只是保护措施，不应因此影响业务
            return true;
        }
    }

    /**
     * 将指定的时间单位转换为毫秒
     *
     * 例如：
     * - convertToMillis(10, TimeUnit.SECONDS) 返回 10000
     * - convertToMillis(1, TimeUnit.MINUTES) 返回 60000
     * - convertToMillis(100, TimeUnit.MILLISECONDS) 返回 100
     *
     * @param time 时间数值
     * @param timeUnit 时间单位（支持毫秒、秒、分钟、小时、天）
     * @return 转换后的毫秒数
     */
    private long convertToMillis(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time);
    }

}
