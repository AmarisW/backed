package com.gqtian.contrast.task;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import cn.hutool.cache.impl.TimedCache;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 测试任务
 * @className TestTask
 * @author gqtian
 * @date 2026/1/14 20:08
 */
@Slf4j
@Component
public class TestTask {

    /**
     * 定时缓存
     */
    public static final TimedCache<Integer, Integer> TIME_DATA_CACHE =
        new TimedCache<>(TimeUnit.SECONDS.toMillis(20));

    /*
      定时清理缓存 每分钟清理一次过期缓存（建议一定设置！）
     */
    static {
        TIME_DATA_CACHE.put(1, 1);
        TIME_DATA_CACHE.put(2, 2);
        TIME_DATA_CACHE.schedulePrune(TimeUnit.SECONDS.toMillis(10));
    }

    /**
     * 测试方法
     */
    // @Scheduled(fixedRate = 5000)
    public void test() {
        Integer value = TIME_DATA_CACHE.get(1, false);
        if (value == null) {
            log.warn("缓存key=1已过期，重新放入缓存");
            TIME_DATA_CACHE.put(1, 1);
            TIME_DATA_CACHE.put(2, 2);
        } else {
            log.info("缓存key=1的值：{}", value);
        }
    }
}
