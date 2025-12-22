package com.gqtian.redis.interceptor;

import static com.gqtian.redis.constant.SystemConstant.REDIS_KEY.LEAKY_BUCKET_LIMIT;

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
 * 漏桶算法限流拦截器
 *
 * 基于漏桶（Leaky Bucket）算法实现的令牌桶限流，不使用Lua脚本，通过Redis String存储状态。
 * 相比固定窗口和滑动窗口，漏桶能够以恒定速率处理请求，并支持短暂的突发流量。
 *
 * 核心优势：
 * 1. 恒定速率：按固定速率漏水（处理请求），流出的水量恒定
 * 2. 流量平滑：突发流量可以先存储在桶中，然后以固定速率处理
 * 3. 突发容量：支持短时间的突发请求，避免突然流量尖峰
 *
 * 工作流程：
 * 1. 拦截请求，检查是否有@RequestLimit注解
 * 2. 从请求参数中获取token作为限流标识
 * 3. 计算当前可用的桶容量（已漏出的水量）
 * 4. 若容量足够则允许请求并记录请求时间，否则拒绝
 *
 * 数据存储结构：
 * - Redis Key: "leaky_bucket_limit:{token}"
 * - 存储格式: "currentSize:lastLeakTime" (以冒号分隔)
 * - currentSize: 当前桶中的请求数
 * - lastLeakTime: 上次计算漏水的时间戳(毫秒)
 * - 漏水速率: capacity / timeout (请求数/时间窗口)
 *
 * 漏桶算法原理：
 * 1. 桶有固定容量，代表最多可缓存的请求数
 * 2. 水以恒定速率漏出：漏出请求数 = (当前时间 - 上次计算时间) * 漏水速率
 * 3. 新请求到达时：
 *    - 首先计算漏出了多少请求
 *    - 更新当前桶中的请求数
 *    - 如果桶未满，则加入新请求；否则拒绝
 *
 * @className LeakyBucketLimitInterceptor
 * @author gqtian
 * @date 2025/12/19 13:36
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LeakyBucketLimitInterceptor implements HandlerInterceptor {

    // Redis模板，用于操作漏桶中的状态数据
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 前置请求拦截处理
     *
     * 在请求到达Controller前进行漏桶限流检查。如果方法有@RequestLimit注解，则执行漏桶限流验证。
     * 若请求被限流拒绝，直接抛出GlobalException，不会到达业务处理器。
     *
     * @param request HTTP请求对象，用于获取请求参数和token
     * @param response HTTP响应对象，用于返回错误信息
     * @param handler 请求处理器（Controller方法）
     * @return true 表示请求通过限流检查，继续处理；false 表示请求被中断（本实现中异常时返回true)
     * @throws Exception 当token为空或请求被限流拒绝时抛出GlobalException
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
                    throw new GlobalException("【漏桶限流】请求标识为空");
                }

                // 执行限流检查
                if (checkLeakyBucketLimit(token, requestLimit)) {
                    // 请求通过限流检查，记录日志并允许继续处理
                    log.info("【漏桶限流】请求检查通过，token: {}", token);
                    return true;
                } else {
                    // 请求被限流拒绝，记录警告日志
                    log.warn("【漏桶限流】请求被拒绝（桶满），token: {}，限流设置: {}个容量/{}{}", token, requestLimit.limitTimes(),
                        requestLimit.timeout(), RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                    // 抛出异常，返回给客户端限流拒绝的提示信息
                    throw new GlobalException(
                        "【漏桶限流】请求被拒绝，token: " + token + "，限流设置: " + requestLimit.limitTimes() + "个容量/"
                            + requestLimit.timeout() + RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
                }
            }
        }
        return true;
    }

    /**
     * 检查漏桶限流
     *
     * 核心算法（共4步）：
     * 1. 从Redis获取漏桶当前状态（桶内请求数和上次计算时间）
     * 2. 根据经过时间计算漏出的请求数（恒定速率）
     * 3. 更新桶内请求数：current = max(0, current - leakedCount)
     * 4. 判断新请求是否能加入：if (currentSize < capacity) 允许并加入
     *
     * 时间复杂度：
     * - get: O(1) - 常数时间
     * - set: O(1) - 常数时间
     * - expire: O(1) - 常数时间
     *
     * 内存占用：约50字节/token标识 (自动过期，无内存泄漏风险)
     *
     * @param token 限流标识（通常为用户ID或IP地址）
     * @param limit @RequestLimit注解配置，包含limitTimes(桶容量)、timeout、timeUnit
     * @return true 表示请求通过限流检查（桶未满）；false 表示桶满（请求被拒绝）
     */
    public boolean checkLeakyBucketLimit(String token, RequestLimit limit) {
        // 生成Redis Key，用于存储该token对应的漏桶状态
        String key = LEAKY_BUCKET_LIMIT + token;

        try {
            // 获取当前系统时间戳（毫秒级精度）
            long currentTime = System.currentTimeMillis();

            // ============ 步骤1：获取漏桶当前状态 ============
            // 从Redis中获取漏桶数据，格式："currentSize:lastLeakTime"
            String bucketData = redisTemplate.opsForValue().get(key);

            // 初始化桶内请求数和上次漏水时间
            long currentSize;
            long lastLeakTime;

            if (bucketData == null || bucketData.isEmpty()) {
                // 首次请求：初始化漏桶（空桶）
                currentSize = 0;
                lastLeakTime = currentTime;
                log.info("【漏桶限流】初始化漏桶，token: {}，容量: {}个请求/{}{}", token, limit.limitTimes(), limit.timeout(),
                    RedisCommonUtil.timeUnitDesc(limit.timeUnit()));
            } else {
                // 解析存储的漏桶数据
                String[] parts = bucketData.split(":");
                currentSize = Long.parseLong(parts[0]);
                lastLeakTime = Long.parseLong(parts[1]);
            }

            // ============ 步骤2：计算漏出的请求数 ============
            // 计算距离上次漏水经过的时间（毫秒）
            long elapsedTime = currentTime - lastLeakTime;

            // 将超时时间转换为毫秒（用于计算漏水速率）
            long timeoutMillis = RedisCommonUtil.convertToMillis(limit.timeout(), limit.timeUnit());

            // 计算漏水速率：请求数/时间 (例：1秒内处理10个请求，则速率为0.01请求/毫秒)
            // 漏出请求数 = 经过时间 * (容量 / 时间窗口)
            // 例如：容量100，窗口10秒，经过5秒，则漏出 5 * (100/10000ms) = 50个请求
            long leakedCount = 0;
            if (elapsedTime > 0 && timeoutMillis > 0) {
                leakedCount = (elapsedTime * limit.limitTimes()) / timeoutMillis;
            }

            // ============ 步骤3：更新桶内请求数 ============
            // 从桶中漏出请求（至少为0）
            currentSize = Math.max(0, currentSize - leakedCount);

            // ============ 步骤4：判断新请求是否能加入 ============
            if (currentSize < limit.limitTimes()) {
                // 桶未满，允许新请求加入
                currentSize++;

                // 将更新后的状态保存回Redis
                String newBucketData = currentSize + ":" + currentTime;
                redisTemplate.opsForValue().set(key, newBucketData);

                // 设置Redis key的过期时间，防止内存泄漏
                long expireTime = RedisCommonUtil.convertToMillis(limit.timeout(), limit.timeUnit()) + 1000;
                redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);

                // 请求成功通过限流检查
                log.info("【漏桶限流】请求通过，token: {}，当前桶内: {}个请求，容量: {}个", token, currentSize, limit.limitTimes());
                return true;
            } else {
                // 桶满，拒绝请求
                log.warn("【漏桶限流】请求被拒绝，token: {}，当前桶内: {}个请求，容量: {}个", token, currentSize, limit.limitTimes());
                return false;
            }
        } catch (Exception e) {
            // 异常处理：Redis操作失败时
            log.error("【漏桶限流】检查限流异常，token: {}", token, e);
            // 策略：异常时放行请求，避免限流成为新的系统瓶颈
            return true;
        }
    }

}
