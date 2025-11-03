package com.gqtian.rocket.rmq;

/**
 * 😶‍🌫️ 消费者接口
 *
 * @author gqtian
 * @className ConsumerPlatformService
 * @date 2024/5/13 14:47
 */
public interface ServerInterface {

    /**
     * 启动
     */
    void start(String id);

    /**
     * 下线
     */
    void shutdown(String id);

    /**
     * 重启
     */
    void restart(String id);
}
