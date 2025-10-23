package com.gqtian.hbase.listener;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/***
 * 👻默认消息处理类**
 * 
 * @author gqtian*
 * @className DefaultMessageHandle*
 * @date 2024/5/13 14:29
 */
@Slf4j
@Service
@ConditionalOnExpression("${rocketmq.consumer.enable:false}")
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", selectorExpression = "${rocketmq.consumer.tags}",
    consumerGroup = "${rocketmq.consumer.group}")
public class DefaultMessageListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String result = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("接收到消息:{}", result);
    }
}
