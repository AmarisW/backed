package com.gqtian.rocket.rmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.stereotype.Service;

import com.gqtian.rocket.cache.RocketMqCache;
import com.gqtian.rocket.init.ConsumerInit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消费者服务类，用于管理和启动RocketMQ消费者。
 */
@Slf4j
@Service
@AllArgsConstructor
public class ConsumerService implements ServerInterface {

    private final RocketClientProperties rocketClientProperties;

    private final RocketMqCache rocketMqCache;

    private final ConsumerInit consumerInit;

    /**
     * 启动指定ID的消费者。
     *
     * @param rocketId 消费者组的ID
     */
    @Override
    public void start(String rocketId) {
        try {
            // 检查消费者是否已经启动
            if (rocketMqCache.getConsumers().containsKey(rocketId)) {
                log.info("消费者:{}已存在,无需启动", rocketId);
                return;
            }
            if (rocketClientProperties.getConsumerList().stream()
                .anyMatch(consumerInfo -> consumerInfo.getGroupName().equals(rocketId))) {
                // 根据ID查找并创建消费者
                rocketClientProperties.getConsumerList().stream()
                    .filter(consumerInfo -> consumerInfo.getGroupName().equals(rocketId)).findFirst()
                    .ifPresent(consumerInit::creatConsumer);
            } else {
                log.info("未找到指定的消费者信息:{}不存在", rocketId);
            }

        } catch (Exception e) {
            log.error("消费者:{}启动失败", rocketId);
        }

    }

    /**
     * 关闭指定ID的消费者。
     *
     * @param rocketId 消费者组的ID
     */
    @Override
    public void shutdown(String rocketId) {
        synchronized (rocketMqCache) { // 确保线程安全
            if (rocketMqCache.getConsumers().containsKey(rocketId)) {
                DefaultMQPushConsumer consumer = rocketMqCache.getConsumers().get(rocketId);
                // consumer.suspend(); // 暂停消费
                // consumer.resume();// 恢复消费
                consumer.shutdown(); // 关闭消费者
                rocketMqCache.removeConsumer(rocketId);
                log.info("消费者:{}关闭成功", rocketId);
            } else {
                log.info("消费者:{}不存在", rocketId);
            }
        }
    }

    /**
     * 重启指定ID的消费者。
     *
     * @param rocketId 消费者组的ID
     */
    @Override
    public void restart(String rocketId) {
        log.info("消费者:{}重启中...", rocketId);
        shutdown(rocketId); // 先关闭
        start(rocketId); // 再启动
        log.info("重启消费者:{}成功", rocketId);
    }

}
