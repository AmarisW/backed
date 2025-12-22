package com.gqtian.redis.aspect;

import static com.gqtian.redis.constant.SystemConstant.REDIS_KEY.AOP_LIMIT;

import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.redis.annotation.RequestLimitAop;
import com.gqtian.redis.utils.RedisUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 接口限流Aop接口限流操作
 * 
 * 基于Redis实现的请求限流AOP切面，使用计数器算法限制指定时间内的请求次数。
 * 通过@RequestLimit注解标注的方法会被此切面拦截，并根据配置的限流规则进行请求数统计。
 * 
 * 工作流程：
 * 1. 从request中提取客户端IP地址作为识别标识
 * 2. 使用"limit:{name}:{ip}"格式作为Redis key
 * 3. 每次请求时从Redis获取当前计数器值，若不存在则设置为0
 * 4. 若当前计数器已达到限制次数(limitTimes)，则拒绝请求并抛出异常
 * 5. 若未达到限制，计数器+1，并按指定时长设置过期时间
 * 
 * 限流陷阱注意：
 * - getExpire()返回秒单位，set()方法的timeUnit需要与参数一致
 * - 必须验证过期时间的有效性，禁止传入负数给SET命令
 * 
 * @className RequestLimitAspect
 * @author gqtian
 * @date 2025/12/18 16:15
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLimitAspect {

    private final RedisUtil redisUtil;

    /**
     * 环绕通知：在被@RequestLimit标注的方法执行前后进行限流检查
     * 
     * @param joinPoint 切点信息，包含方法和参数等上下文
     * @return 方法执行结果
     * @throws Throwable 当请求被限流或方法执行异常时抛出
     */
    @Around("@annotation(com.gqtian.redis.annotation.RequestLimitAop)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 1. 获取当前HTTP请求对象，用于提取客户端ip
            ServletRequestAttributes attributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(attributes)) {
                log.warn("【Aop限流】无法获取request对象，跳过限流检查");
                return joinPoint.proceed();
            }
            HttpServletRequest request = attributes.getRequest();

            // 2. 获取目标方法上的@RequestLimit注解
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            RequestLimitAop requestLimit = method.getAnnotation(RequestLimitAop.class);

            // 3. 从注解中获取限流配置参数
            String limitName = requestLimit.name();
            int limitTimes = requestLimit.limitTimes();
            long timeout = requestLimit.timeout();
            // 获取超时时间单位，然后转换为秒（Redis统一使用秒作为单位）
            long timeoutInSeconds = requestLimit.timeUnit().toSeconds(timeout);

            // 4. 验证超时时间的有效性，禁止将负数传入Redis
            if (timeoutInSeconds <= 0) {
                log.error("【Aop限流】限流超时时间无效：name={}, timeout={}, timeUnit={}", limitName, timeout,
                    requestLimit.timeUnit());
                throw new GlobalException("【Aop限流】限流配置错误：超时时间必须大于0");
            }

            // region 5.Token获取方式
            String token = StringUtils.EMPTY;
            // 请求参数中获取
            token = request.getParameter(requestLimit.name());

            // 从请求头中获取{
            // token = request.getHeader(requestLimit.name());
            // endregion

            String redisKey = AOP_LIMIT + limitName + token;

            // 6. 从Redis获取当前请求计数器值
            Object currentCountObj = redisUtil.get(redisKey);
            long currentCount = currentCountObj == null ? 0 : Long.parseLong(String.valueOf(currentCountObj));

            // 7. 检查是否已超过限流阈值
            if (currentCount >= limitTimes) {
                log.warn("【Aop限流】请求被限流：token={}, currentCount={}, limitTimes={}", token,
                    currentCount, limitTimes);
                throw new GlobalException("【Aop限流】请求过于频繁，已被限流");
            }

            // 8. 若未超限，则计数器+1，并设置过期时间
            // 首次请求时设置过期时间，后续请求会在过期后重置
            if (currentCount == 0) {
                redisUtil.set(redisKey, 1L, timeoutInSeconds);
            } else {
                // 非首次请求，仅增加计数器，不更新过期时间
                redisUtil.incr(redisKey, 1);
            }

            // 9. 请求通过限流检查，执行目标方法
            return joinPoint.proceed();
        } catch (GlobalException e) {
            // GlobalException是业务异常，直接抛出
            throw e;
        } catch (Throwable e) {
            // 其他异常记录日志后重新抛出
            log.error("【Aop限流】限流 Aop 处理异常", e);
            throw e;
        }
    }

}
