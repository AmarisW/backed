package com.gqtian.rabbit.consumer;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.gqtian.rabbit.config.RabbitMqConfig;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 死信队列消费者。
 * <p>
 * 对象业务队列 {@link RabbitMqConfig#DIRECT_QUEUE_OBJECT} 在 nack(requeue=false)、
 * 消息过期或队列满时，消息会进入死信交换机再落到本队列。
 * </p>
 * <p>
 * 使用原始 {@link Message} 接收入参，避免死信体（如 OrderMessage JSON）被强转为 String 失败；
 * body 按 UTF-8 打日志便于排查。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Component
public class DeadLetterConsumer {

    /**
     * 处理死信：记录 body 与 {@code x-death}，成功后 ACK。
     * <p>
     * 本队列无更外层 DLX；处理失败时使用 requeue=true，避免 {@code nack(requeue=false)} 永久丢消息。
     * 若属业务永久失败，应落库/告警后 ACK，或转发人工队列。
     * </p>
     *
     * @param message     原始 AMQP 消息
     * @param channel     通道
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = RabbitMqConfig.DLX_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onDeadLetter(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
        throws Exception {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            // x-death：原始队列、进入死信原因、时间戳等
            Object xDeath = message.getMessageProperties().getHeaders().get("x-death");
            log.warn("[死信] 收到死信消息: {}, contentType={}, 死信信息: {}", body,
                message.getMessageProperties().getContentType(), xDeath);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[死信] 处理失败，requeue=true 避免直接丢弃", e);
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
