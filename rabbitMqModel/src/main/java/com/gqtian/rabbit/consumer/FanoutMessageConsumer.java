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
        try {
            log.info("[扇出-A] 消费消息: {}", body);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[扇出-A] 消费失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.FANOUT_QUEUE_B, containerFactory = "rabbitListenerContainerFactory")
    public void onFanoutB(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            log.info("[扇出-B] 消费消息: {}", body);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[扇出-B] 消费失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
