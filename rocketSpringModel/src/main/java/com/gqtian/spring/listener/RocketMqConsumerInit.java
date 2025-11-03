
package com.gqtian.spring.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * 👻消费者初始化**
 * 
 * @author gqtian*
 * @className ConsumerInit*
 * @date 2024/5/13 14:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class RocketMqConsumerInit {
    private final RocketMqProperties rocketMqProperties;

    @PostConstruct
    public void init() {
        List<RocketMqProperties.ConsumerInfo> consumerList = rocketMqProperties.getConsumerList();
        if (CollectionUtils.isEmpty(consumerList)) {
            log.error("未配置消费者信息");
            return;
        }
        consumerList.forEach(this::creatConsumer);
    }

    /**
     * 创建并启动一个RocketMQ消费者。
     *
     * @param consumer 消费者配置信息
     */
    public void creatConsumer(RocketMqProperties.ConsumerInfo consumer) {
        try {

            // 检测是否开启消费
            if (!consumer.getEnabled()) {
                log.error("消费 {} 未开启消费！", consumer.getListener());
                return;
            }

            DefaultMQPushConsumer consumerStart = createMQPushConsumer(consumer);

            // 订阅 Topic
            subscribeTopics(consumerStart, consumer.getTopics());

            // 注册消息监听器
            consumerStart.registerMessageListener((MessageListenerConcurrently)(messageList, context) -> {
                messageList.forEach(messageExt -> {
                    log.debug("收到消息,topic：{}，tag：{}，消息来源：{}，msgId：{}", messageExt.getTopic(), messageExt.getTags(),
                        messageExt.getBornHost(), messageExt.getMsgId());
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            consumerStart.start();

            log.info("消费组{} 消费者初始化成功, topics: {}", consumer.getGroupName(), consumer.getTopics());
        } catch (Exception e) {
            log.error("启动消费者失败: {}", consumer != null ? consumer.getGroupName() : "未知组名", e);
            throw new RuntimeException("RocketMQ消费者初始化失败", e);
        }
    }

    /**
     * 创建消费者基础配置
     *
     * @param consumer 消费者配置信息
     * @return DefaultMQPushConsumer
     */
    private DefaultMQPushConsumer createMQPushConsumer(RocketMqProperties.ConsumerInfo consumer) {
        DefaultMQPushConsumer consumerStart = new DefaultMQPushConsumer(consumer.getGroupName());
        consumerStart.setNamesrvAddr(rocketMqProperties.getNameSrvAddr());
        consumerStart.setConsumeThreadMin(rocketMqProperties.getConsumeThreadMin());
        consumerStart.setConsumeThreadMax(rocketMqProperties.getConsumeThreadMax());
        consumerStart.setConsumeMessageBatchMaxSize(rocketMqProperties.getConsumeMessageBatchMaxSize());
        consumerStart.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumerStart.setMessageModel(MessageModel.CLUSTERING);
        return consumerStart;
    }

    /**
     * 订阅多个 Topic
     */
    private void subscribeTopics(DefaultMQPushConsumer consumer, List<RocketMqProperties.TopicInfo> topics) {
        for (RocketMqProperties.TopicInfo topic : topics) {
            String tag = StringUtils.isBlank(topic.getTag()) ? "*" : topic.getTag();
            try {
                consumer.subscribe(topic.getTopic(), tag);
            } catch (Exception e) {
                log.error("订阅 Topic {} 失败", topic.getTopic(), e);
            }
        }
    }
}