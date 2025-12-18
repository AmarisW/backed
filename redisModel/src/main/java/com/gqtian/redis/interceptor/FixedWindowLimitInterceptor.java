package com.gqtian.redis.interceptor;

import static com.gqtian.redis.constant.SystemConstant.REDIS_KEY.FIXED_WINDOW_LIMIT;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundValueOperations;
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
 * 请求限流拦截器
 * 
 * 基于Redis实现的接口级别请求限流,通过@RequestLimit注解标记需要限流的接口,
 * 使用token作为限流的唯一标识,支持自定义限流次数和时间窗口。
 * 
 * 限流原理:
 * 1. 从请求参数中获取token作为限流标识
 * 2. 在Redis中维护token对应的请求计数
 * 3. 每次请求时检查计数是否超过限制
 * 4. 若超过限制则拒绝请求,否则计数+1并重置过期时间
 * 
 * @className FixedWindowLimitInterceptor
 * @author gqtian
 * @date 2025/12/18 11:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixedWindowLimitInterceptor implements HandlerInterceptor {

    // Redis模板,用于操作Integer类型的计数器
    private final RedisTemplate redisTemplate;

    /**
     * 前置处理拦截器
     * 
     * 在请求处理前进行限流检查,若token为空或超过限制次数则拒绝请求,
     * 否则通过请求并更新Redis中的请求计数。
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  请求处理器
     * @return true表示继续处理请求,false表示中断请求
     * @throws Exception 处理过程中可能出现的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            // 检查方法是否添加 RequestLimit 注解
            if (handlerMethod.hasMethodAnnotation(RequestLimit.class)) {
                RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);

                // region Token获取方式
                String token = StringUtils.EMPTY;
                // 请求参数中获取
                token = request.getParameter(requestLimit.name());

                // 从请求头中获取{
                // token = request.getHeader(requestLimit.name());
                // endregion

                // token 验证
                if (StringUtils.isEmpty(token)) {
                    throw new GlobalException("【限流】请求限流:标识为空");
                }

                // 限流检查
                if (checkLimit(token, requestLimit)) {
                    log.info("【限流】请求限流检查通过,token: {}", token);
                    return true;
                } else {
                    log.warn("【限流】请求被限流拒绝,token: {},限流设置: 每{}{}{}次", token, requestLimit.timeout(),
                        RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()), requestLimit.limitTimes());
                    throw new GlobalException("【限流】请求被限流拒绝,token: " + token + ",限流设置: 每"
                        + requestLimit.timeout() + RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit())
                        + requestLimit.limitTimes() + "次");
                }
            }
        }
        return true;
    }

    /**
     * 限流检查
     * 
     * 根据token在Redis中维护的请求计数,判断当前请求是否超过限流次数。
     * 工作流程:
     * 1. 若Redis中不存在该token的计数,则初始化计数为1,设置过期时间
     * 2. 若计数超过限制,返回false拒绝请求
     * 3. 否则计数+1,重置过期时间,允许请求
     *
     * @param token 限流标识（通常为用户ID或IP地址）
     * @param requestLimit @RequestLimit注解信息,包含限流次数和时间窗口
     * @return true表示请求通过限流检查,false表示超过限流次数
     */
    private Boolean checkLimit(String token, RequestLimit requestLimit) {
        String key = FIXED_WINDOW_LIMIT + token;

        BoundValueOperations<String, Integer> boundValueOps = redisTemplate.boundValueOps(key);
        Integer count = boundValueOps.get();

        if (Objects.isNull(count)) {
            // 首次请求,初始化计数
            boundValueOps.set(1, requestLimit.timeout(), requestLimit.timeUnit());
            log.info("【限流】初始化限流计数,token: {}", token);
            return Boolean.TRUE;
        } else if (count >= requestLimit.limitTimes()) {
            // 请求超过限流次数
            log.info("【限流】请求超过限流阈值,token: {},当前计数: {},限流设置: 每{}/({}/{})次", token, count,
                requestLimit.limitTimes(), requestLimit.timeout(),
                RedisCommonUtil.timeUnitDesc(requestLimit.timeUnit()));
            return Boolean.FALSE;
        } else {
            // 计数+1，重置过期时间 boundValueOps.getExpire()单位是秒
            long currentExpireSeconds = boundValueOps.getExpire();
            if (currentExpireSeconds > 0) {
                // 将秒转换为目标时间单位
                long currentExpireInTargetUnit =
                    requestLimit.timeUnit().convert(currentExpireSeconds, TimeUnit.SECONDS);
                boundValueOps.set(++count, currentExpireInTargetUnit, requestLimit.timeUnit());
            } else {
                // 如果获取过期时间失败，使用原始 timeout
                boundValueOps.set(++count, requestLimit.timeout(), requestLimit.timeUnit());
            }
            log.info("【限流】更新限流计数,token: {},当前计数: {}", token, count);
            return Boolean.TRUE;
        }
    }
}
