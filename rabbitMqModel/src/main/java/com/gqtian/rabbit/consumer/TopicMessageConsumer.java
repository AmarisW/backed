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
 * 主题队列消费者（手动 ACK）
 * <p>
 * 匹配规则：order.# — 接收所有 order 开头的消息
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class TopicMessageConsumer {

    @RabbitListener(queues = RabbitMqConfig.TOPIC_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onTopicMessage(@Payload String body, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("[主题] 消费消息, routingKey={}, body={}", routingKey, body);
            // 根据 routingKey 分发处理
            if (routingKey != null && routingKey.startsWith("order.created")) {
                // 处理订单创建
                log.info("[主题] 处理订单创建, routingKey={}, body={}", routingKey, body);
            } else if (routingKey != null && routingKey.startsWith("order.paid")) {
                // 处理订单支付
                log.info("[主题] 处理订单支付, routingKey={}, body={}", routingKey, body);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[主题] 消费失败", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
