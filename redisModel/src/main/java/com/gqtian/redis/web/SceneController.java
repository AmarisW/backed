package com.gqtian.redis.web;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.redis.annotation.RequestLimit;
import com.gqtian.redis.annotation.RequestLimitAop;
import com.gqtian.redis.constant.SystemConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 业务场景控制器
 * @className SceneController
 * @author gqtian
 * @date 2025/12/18 11:06
 */
@Slf4j
@RestController
@RequestMapping("/scene")
@RequiredArgsConstructor
public class SceneController {

    /**
     * Aop 请求限流
     */
    @RequestLimitAop(name = SystemConstant.TOKEN, limitTimes = 5, timeout = 10, timeUnit = TimeUnit.SECONDS)
    @RequestMapping("/aopLimit")
    public String aopLimit(String token) {
        return "Aop限流 请求成功: " + token;
    }

    /** 
     * 固定窗口请求限流
     * @return 结果
     */
    @RequestLimit(name = SystemConstant.TOKEN, limitTimes = 5, timeout = 10, timeUnit = TimeUnit.SECONDS)
    @RequestMapping("/FixedWindowLimit")
    public String FixedWindowLimit(String token) {
        return "固定窗口限流 请求成功: " + token;
    }

    /**
     * 滑动窗口请求限流
     * @return 结果
     */
    @RequestLimit(name = SystemConstant.TOKEN, limitTimes = 5, timeout = 10, timeUnit = TimeUnit.SECONDS)
    @GetMapping("/slidingWindowLimit")
    public String slidingWindowLimit(String token) {
        return "滑动窗口限流 请求成功: " + token;
    }
}
