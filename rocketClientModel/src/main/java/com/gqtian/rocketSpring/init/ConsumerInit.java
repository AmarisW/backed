package com.gqtian.rocketSpring.init;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSON;
import com.gqtian.rocketSpring.cache.RocketMqCache;
import com.gqtian.rocketSpring.handle.DefaultMessageHandle;
import com.gqtian.rocketSpring.handle.MessageHandleInterface;
import com.gqtian.rocketSpring.rmq.RocketClientProperties;
import com.gqtian.rocketSpring.rmq.RocketClientProperties.TopicInfo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 消费者初始化
 *
 * @author gqtian
 * @className ConsumerInit
 * @date 2024/5/13 14:35
 */
@Slf4j
@Component
@Order(10)
@AllArgsConstructor
public class ConsumerInit implements ApplicationRunner {

    private final static String BROADCASTING_STRING = "BROADCASTING";

    private final RocketClientProperties rocketClientProperties;

    private final Map<String, MessageHandleInterface> messageHandleMap;

    private final RocketMqCache rocketMqCache;

    @Override
    public void run(ApplicationArguments args) {
        List<RocketClientProperties.ConsumerInfo> consumerList = rocketClientProperties.getConsumerList();
        if (CollectionUtils.isEmpty(consumerList)) {
            log.error("未配置消费者信息");
        }
        consumerList.forEach(this::creatConsumer);
    }

    /**
     * 创建并启动一个RocketMQ消费者。
     *
     * @param consumer 消费者配置信息
     */
    public void creatConsumer(RocketClientProperties.ConsumerInfo consumer) {
        try {
            // 配置参数校验
            if (consumer == null || StringUtils.isBlank(consumer.getGroupName()) || consumer.getTopics() == null
                || consumer.getTopics().isEmpty()) {
                log.error("消费者配置不正确，请检查配置文件信息！");
                return;
            }

            DefaultMQPushConsumer consumerStart = new DefaultMQPushConsumer(consumer.getGroupName());
            consumerStart.setNamesrvAddr(consumer.getNameSrvAddr());
            consumerStart.setConsumeThreadMin(consumer.getConsumeThreadMin());
            consumerStart.setConsumeThreadMax(consumer.getConsumeThreadMax());
            consumerStart.setConsumeMessageBatchMaxSize(consumer.getConsumeMessageBatchMaxSize());
            consumerStart.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

            // 设置消费模型
            consumerStart.setMessageModel(StringUtils.equals(BROADCASTING_STRING, consumer.getMessageModel())
                ? MessageModel.BROADCASTING : MessageModel.CLUSTERING);

            // 订阅topic
            for (TopicInfo topic : consumer.getTopics()) {
                consumerStart.subscribe(topic.getTopic(), StringUtils.isBlank(topic.getTag()) ? "*" : topic.getTag());
            }

            // 注册消息监听器
            consumerStart.registerMessageListener((MessageListenerConcurrently)(messageList, context) -> {
                try {
                    messageList.forEach(messageExt -> this.consumerMessage(consumer,
                        new String(messageExt.getBody(), StandardCharsets.UTF_8)));
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    log.error("消息处理失败", e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            });

            consumerStart.start();
            rocketMqCache.putConsumer(consumer.getGroupName(), consumerStart);
            log.info("消费者初始化成功,消费者信息:{}", JSON.toJSON(consumer));
        } catch (Exception e) {
            log.error("启动消费者失败", e);
        }
    }

    /**
     * 处理接收到的消息。
     *
     * @param consumer 消费者配置信息
     * @param message 接收到的消息内容
     */
    public void consumerMessage(RocketClientProperties.ConsumerInfo consumer, String message) {
        try {
            String topicList = consumer.getTopics().stream().map(topic -> topic.getTopic() + ":" + topic.getTag())
                .collect(Collectors.joining(" | "));
            // 尝试根据消费者组ID获取消息处理接口实现
            MessageHandleInterface messageHandle =
                messageHandleMap.getOrDefault(consumer.getGroupName(), new DefaultMessageHandle());
            messageHandle.messageHandle(message);
            log.info("消费者:{},消费组件:{},消费消息详情:{}", consumer.getGroupName(), topicList, JSON.toJSON(message));
        } catch (Exception e) {
            log.error("消息处理失败", e);
        }
    }

}
