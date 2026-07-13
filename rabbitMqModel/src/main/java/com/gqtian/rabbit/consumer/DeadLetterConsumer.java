package com.gqtian.rabbit.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.gqtian.rabbit.config.RabbitMqConfig;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 死信队列消费者
 * <p>
 * 当 {@link RabbitMqConfig#DIRECT_QUEUE_OBJECT} 消息被 nack（requeue=false）、消息过期、队列满时，
 * 会被路由到死信交换机，最终进入死信队列
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class DeadLetterConsumer {

    @RabbitListener(queues = RabbitMqConfig.DLX_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onDeadLetter(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            // 死信消息的 x-death header 中包含原始队列、原因、时间戳等信息
            Object xDeath = message.getMessageProperties().getHeaders().get("x-death");
            log.warn("[死信] 收到死信消息: {}, 死信信息: {}", body, xDeath);
            // 死信处理：人工补偿、告警、持久化到数据库等
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[死信] 处理失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
