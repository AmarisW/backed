package com.gqtian.rocketSpring.handle;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 默认消息处理类
 *
 * @author gqtian
 * @className DefaultMessageHandle
 * @date 2024/5/13 14:29
 */
@Slf4j
@Service
public class DefaultMessageHandle implements MessageHandleInterface {

    @Override
    public void messageHandle(String message) {
        log.info("默认消息处理类:{}", JSON.toJSON(message));
    }
}
