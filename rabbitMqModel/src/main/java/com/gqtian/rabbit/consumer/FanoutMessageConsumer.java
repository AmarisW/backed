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
 * 扇出队列消费者（广播）。
 * <p>同一条消息会分别投递到 A、B 两个队列，各自独立 ACK。</p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class FanoutMessageConsumer {

    /**
     * 消费扇出队列 A。
     *
     * @param body        消息体
     * @param message     原始消息
     * @param channel     通道
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.FANOUT_QUEUE_A, containerFactory = "rabbitListenerContainerFactory")
    public void onFanoutA(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        handle("A", body, channel, deliveryTag);
    }

    /**
     * 消费扇出队列 B。
     *
     * @param body        消息体
     * @param message     原始消息
     * @param channel     通道
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.FANOUT_QUEUE_B, containerFactory = "rabbitListenerContainerFactory")
    public void onFanoutB(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        handle("B", body, channel, deliveryTag);
    }

    /**
     * A/B 共用处理逻辑，仅用 tag 区分日志来源。
     *
     * @param tag         队列标识（A / B）
     * @param body        消息体
     * @param channel     通道
     * @param deliveryTag 投递标签
     */
    private void handle(String tag, String body, Channel channel, long deliveryTag) throws Exception {
        try {
            log.info("[扇出-{}] 消费消息: {}", tag, body);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[扇出-{}] 消费失败", tag, e);
            // 未配置 DLX：requeue=false 直接丢弃
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
