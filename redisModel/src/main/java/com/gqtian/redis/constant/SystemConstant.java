package com.gqtian.redis.constant;

/**
 * 🔔 全局静态变量
 * 
 * @author GQTIAN
 * @date 2025/7/24 17:15
 **/
public class SystemConstant {

    public static final String TOKEN = "token";

    /**
     * Redis Key
     */
    public interface REDIS_KEY {

        /**
         * Aop 限流
         */
        String AOP_LIMIT = "Aop_limit:";

        /**
         * 固定窗口限流
         */
        String FIXED_WINDOW_LIMIT = "fixed_window_limit:";

        /**
         * 滑动窗口限流
         */
        String SLIDING_WINDOW_LIMIT = "sliding_window_limit:";
    }
}
