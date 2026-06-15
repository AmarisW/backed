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
 * 直连队列消费者（手动 ACK 模式）
 *
 * @author gqtian
 */
@Slf4j
@Component
public class DirectMessageConsumer {

    /**
     * 消费字符串消息
     */
    @RabbitListener(queues = RabbitMqConfig.DIRECT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onStringMessage(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            log.info("[直连-字符串] 消费消息: {}, messageId={}", body, message.getMessageProperties().getMessageId());
            // 业务处理...
            // 手动 ACK
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[直连-字符串] 消费失败", e);
            // 拒绝消息，requeue=false 表示不重新入队，进入死信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 消费对象消息（JSON 反序列化）
     */
    @RabbitListener(queues = RabbitMqConfig.DIRECT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onOrderMessage(@Payload OrderMessage order, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            log.info("[直连-订单] 消费订单: {}", order);
            // 业务处理：比如更新订单状态、扣减库存等
            // doBusiness(order);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[直连-订单] 消费失败, orderId={}", order.getOrderId(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
