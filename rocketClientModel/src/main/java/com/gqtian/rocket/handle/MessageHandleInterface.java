package com.gqtian.rocket.handle;

/**
 * 😶‍🌫️ 消费者默认处理类
 * 
 * @className ConsumerHandleInterface
 * @author gqtian
 * @date 2024/5/13 14:28
 */
public interface MessageHandleInterface {

    void messageHandle(String message);

    default boolean messageHandleMethod(String message) {
        return true;
    }
}
