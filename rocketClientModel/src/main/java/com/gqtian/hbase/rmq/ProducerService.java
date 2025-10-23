package com.gqtian.hbase.rmq;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.hbase.cache.RocketMqCache;
import com.gqtian.hbase.init.ProducerInit;

import cn.hutool.core.io.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 消费者实现
 *
 * @author gqtian
 * @className ConsumerService
 * @date 2024/5/13 14:50
 */
@Slf4j
@Service
@AllArgsConstructor
public class ProducerService implements ServerInterface {

    private static volatile boolean IS_SEND = false;

    private final RocketClientProperties rocketClientProperties;

    private final RocketMqCache rocketMqCache;

    private final ProducerInit producerInit;

    // 在类中添加成员变量
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
        new BasicThreadFactory.Builder().namingPattern("RocketMQ-File-Sender-%d").daemon(true).build());

    @Override
    public void start(String rocketId) {
        if (rocketMqCache.getProducers().containsKey(rocketId)) {
            DefaultMQProducer producer = rocketMqCache.getProducers().get(rocketId);
            log.info("生产者已启动,无需启动:{}", JSON.toJSON(producer));
            return;
        }
        try {
            if (rocketClientProperties.getProducerList().stream()
                .anyMatch(producerInfo -> producerInfo.getProducerId().equals(rocketId))) {
                rocketClientProperties.getProducerList().stream()
                    .filter(producerInfo -> producerInfo.getProducerId().equals(rocketId)).findFirst()
                    .ifPresent(producerInit::creatProducer);
            } else {
                log.info("配置文件-未找到指定的生产者信息:{}不存在", rocketId);
                throw new GlobalException("配置文件-未找到指定的生产者信息");
            }
        } catch (Exception e) {
            log.error("创建生产者:{}失败，原因：{}", rocketId, e.getMessage(), e);
        }
    }

    @Override
    public void shutdown(String rocketId) {
        if (rocketMqCache.getProducers().containsKey(rocketId)) {
            rocketMqCache.getProducers().get(rocketId).shutdown();
            rocketMqCache.removeProducer(rocketId);
            log.info("生产者:{}关闭成功!", rocketId);
        } else {
            log.info("生产者:{}不存在!", rocketId);
        }
    }

    @Override
    public void restart(String rocketId) {
        log.info("生产者:{}重启中...", rocketId);
        shutdown(rocketId);
        start(rocketId);
        log.info("生产者:{}重启成功!", rocketId);
    }

    public void sendMessage(String rocketId, String message, String topic) {
        // 获取生产者实例
        DefaultMQProducer producer = RocketMqCache.producerMap.get(rocketId);
        try {
            producer.send(new Message(topic, "*", "", message.getBytes()), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("发送消息成功，topic: {}, msgId: {}", topic, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("发送消息失败，topic: {},{}", topic, throwable.getMessage(), throwable);
                }
            });
        } catch (Exception e) {
            log.error("发送消息失败，原因：{}", e.getMessage(), e);
        }
    }

    /**
     * 发送文件数据
     * 
     * @param rocketId 生产者id
     * @param filePath 文件路径
     * @param topic 发送主题
     */
    public void sendByFile(String rocketId, String filePath, String topic, boolean isSend) {
        String string = FileUtil.readUtf8String(filePath);
        IS_SEND = isSend;
        while (IS_SEND) {
            sendMessage(rocketId, string, topic);
        }
    }

    /**
     * 定时发送文件数据（改进版）
     * 
     * @param rocketId 生产者ID
     * @param filePath 文件路径
     * @param topic 目标主题
     * @param period 发送间隔（毫秒），null则默认5000ms
     */
    public void sendByFileTime(String rocketId, String filePath, String topic, Long period) {

        // 读取文件内容
        final String content;
        try {
            content = FileUtil.readUtf8String(filePath);
            if (content == null || content.isEmpty()) {
                throw new IllegalArgumentException("文件内容为空");
            }
        } catch (Exception e) {
            log.error("文件读取失败: {}", filePath, e);
            return;
        }

        // 设置间隔时间（默认5秒）
        long interval = period != null ? period * 1000 : 5000L;

        // 定时任务
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                sendMessage(rocketId, content, topic);
            } catch (Exception e) {
                log.error("消息发送失败 [topic: {}]", topic, e);
                // 这里可以添加重试逻辑
            }
        }, 5, interval, TimeUnit.MILLISECONDS);
    }

}
