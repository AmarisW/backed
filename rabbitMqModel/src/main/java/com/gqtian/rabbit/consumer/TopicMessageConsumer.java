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
 * 主题队列消费者（手动 ACK）。
 * <p>队列绑定 {@code order.#}，接收所有以 order 为前缀的多级路由键。</p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class TopicMessageConsumer {

    /**
     * 按收到的 routingKey 做业务分支处理。
     *
     * @param body        消息体
     * @param message     原始消息（读取 receivedRoutingKey）
     * @param channel     通道（手动确认）
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.TOPIC_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onTopicMessage(@Payload String body, Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("[主题] 消费消息, routingKey={}, body={}", routingKey, body);
            if (matches(routingKey, "order.created")) {
                log.info("[主题] 处理订单创建, routingKey={}, body={}", routingKey, body);
            } else if (matches(routingKey, "order.paid")) {
                log.info("[主题] 处理订单支付, routingKey={}, body={}", routingKey, body);
            } else {
                // 已绑定 order.#，未单独分支的键仅记录后 ACK
                log.info("[主题] 未匹配专用分支，默认处理, routingKey={}, body={}", routingKey, body);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[主题] 消费失败", e);
            // 未配置 DLX：requeue=false 直接丢弃
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 判断路由键是否等于 exact，或为其子段前缀（如 order.created.sub）。
     *
     * @param key   实际 routing key，可为 null
     * @param exact 期望前缀段，如 {@code order.created}
     * @return 是否匹配
     */
    private boolean matches(String key, String exact) {
        if (key == null) {
            return false;
        }
        return key.equals(exact) || key.startsWith(exact + ".");
    }
}
