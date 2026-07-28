package com.gqtian.rabbit.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.gqtian.rabbit.config.RabbitMqConfig;
import com.gqtian.rabbit.message.OrderMessage;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 直连队列消费者（手动 ACK）。
 * <p>
 * 字符串队列无 DLX；对象队列配置了死信，失败 nack 后进入
 * {@link RabbitMqConfig#DLX_QUEUE}。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class DirectMessageConsumer {

    /**
     * 消费字符串消息（含延迟消息到期后转发而来的消息）。
     *
     * @param body        消息体
     * @param message     原始 AMQP 消息
     * @param channel     通道（手动确认）
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.DIRECT_QUEUE_STRING, containerFactory = "rabbitListenerContainerFactory")
    public void onStringMessage(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            log.info("[直连-字符串] 消费消息: {}, messageId={}", body, message.getMessageProperties().getMessageId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[直连-字符串] 消费失败", e);
            // 未配置 DLX：requeue=false 直接丢弃，不会进死信
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费订单对象消息（JSON 反序列化）。
     * <p>失败且 {@code nack(requeue=false)} 时进入对象队列绑定的死信队列。</p>
     *
     * @param order       订单
     * @param message     原始 AMQP 消息
     * @param channel     通道（手动确认）
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.DIRECT_QUEUE_OBJECT, containerFactory = "rabbitListenerContainerFactory")
    public void onOrderMessage(@Payload OrderMessage order, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            log.info("[直连-订单] 消费订单: {}", order);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[直连-订单] 消费失败, orderId={}", order != null ? order.getOrderId() : null, e);
            // 拒绝且不重入队 → 进入对象队列配置的 DLX
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
