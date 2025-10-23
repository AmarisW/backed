package com.gqtian.hbase.rmq;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 配置RocketMQ消费者的属性。
 *
 * @author gqtian
 * @className RocketMQConsumer
 * @date 2024/4/30 17:10
 */
@Data
@Component
@ConfigurationProperties(prefix = "rocket")
public class RocketClientProperties {

    /**
     * 保存多个RocketMQ服务器的地址信息。
     */
    private List<ConsumerInfo> consumerList;

    /**
     * 生产者配置信息。
     */
    private List<ProducerInfo> producerList;

    /**
     * ProducerInfo 类用于存储消息生产者的配置信息。
     */
    @Data
    public static class ProducerInfo {

        /**
         * 组名
         */
        private String groupName;

        /**
         * 唯一标识
         */
        private String producerId;

        /**
         * NameServer地址，用于指定TCP协议连接的NameServer地址。
         */
        private String nameSrvAddr;

        /**
         * 发送消息的最大尺寸，默认值为4KB。
         */
        private int maxMessageSize = 1024 * 4;

        /**
         * 发送消息的超时时间，默认值为3秒。
         */
        private int sendMsgTimeOut = 3000;

        /**
         * 当发送消息失败时的重试次数，默认值为2次。
         */
        private int retryTimesWhenSendFailed = 2;
    }

    /**
     * 消费者配置信息。
     */
    @Data
    public static class ConsumerInfo {

        /**
         * 消费者组名。
         */
        private String groupName;

        /**
         * RocketMQ名称服务器地址。
         */
        private String nameSrvAddr;

        /**
         * 消费者订阅的主题列表。
         */
        private List<TopicInfo> topics;

        /**
         * 消费线程池最小线程数。
         */
        private int consumeThreadMin;

        /**
         * 消费线程池最大线程数。
         */
        private int consumeThreadMax;

        /**
         * 每次批量消费消息的最大条数。
         */
        private int consumeMessageBatchMaxSize;

        /**
         * 消息模型，例如CLUSTERING（集群消费）或BROADCASTING（广播消费）。
         */
        private String messageModel;

    }

    /**
     * 主题信息，包含主题名和标签。
     */
    @Data
    public static class TopicInfo {

        /**
         * 主题名。
         */
        private String topic;

        /**
         * 主题的标签。
         */
        private String tag;
    }
}
