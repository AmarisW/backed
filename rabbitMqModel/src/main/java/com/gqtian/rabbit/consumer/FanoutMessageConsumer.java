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
 * 扇出队列消费者（广播模式）
 * <p>
 * 同一个消息会被 A、B 两个队列都收到
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class FanoutMessageConsumer {

    @RabbitListener(queues = RabbitMqConfig.FANOUT_QUEUE_A, containerFactory = "rabbitListenerContainerFactory")
    public void onFanoutA(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        handle("A", body, channel, deliveryTag);
    }

    @RabbitListener(queues = RabbitMqConfig.FANOUT_QUEUE_B, containerFactory = "rabbitListenerContainerFactory")
    public void onFanoutB(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        handle("B", body, channel, deliveryTag);
    }

    /**
     * 扇出消息通用处理：A、B 两个队列共用同一份逻辑，便于维护。
     *
     * @param tag        队列标识（A / B），用于日志区分
     * @param body       消息体
     * @param channel    RabbitMQ 通道（手动 ACK）
     * @param deliveryTag 投递标签
     */
    private void handle(String tag, String body, Channel channel, long deliveryTag) throws Exception {
        try {
            log.info("[扇出-{}] 消费消息: {}", tag, body);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[扇出-{}] 消费失败", tag, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
