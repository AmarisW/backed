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

        /**
         * 令牌桶限流
         */
        String TOKEN_BUCKET_LIMIT = "token_bucket_limit:";

        /**
         * 漏桶限流（基于漏桶算法的令牌桶实现）
         */
        String LEAKY_BUCKET_LIMIT = "leaky_bucket_limit:";
    }

    /**
     * Redis Lua 脚本
     */
    public interface REDIS_LUA_SCRIPT_KEY {

        /**
         * 商品库存 Key 
         */
        String GOODS_KEY = "lua:goods:";

        /**
         * 已下单订单 Key
         */
        String ORDER_KEY = "lua:order:";

        /**
         * 已下单信息流
         */
        String ORDER_STREAM = "lua:order-stream";
    }
}
