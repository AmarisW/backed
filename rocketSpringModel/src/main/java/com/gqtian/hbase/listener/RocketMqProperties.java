package com.gqtian.hbase.listener;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/***
 * 配置RocketMQ消费者的属性。**
 * 
 * @author gqtian
 * @className RocketMQConsumer
 * @date 2024/4/30 17:10
 */
@Data
@Component
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMqProperties {

    /**
     * RocketMQ名称服务器地址。
     */
    private String nameSrvAddr;

    /**
     * accessKey。
     */
    private String accessKey;

    /**
     * secretKey。
     */
    private String secretKey;

    /**
     * 消费线程池最小线程数。
     */
    private int consumeThreadMin = 1;

    /**
     * 消费线程池最大线程数。默认 1
     */
    private int consumeThreadMax = 1;

    /**
     * 每次批量消费消息的最大条数。
     */
    private int consumeMessageBatchMaxSize = 1;

    /**
     * 保存多个RocketMQ服务器的地址信息。
     */
    private List<ConsumerInfo> consumerList;

    /**
     * 消费者配置信息。
     */
    @Data
    public static class ConsumerInfo {

        /**
         * 监听者类别
         */
        private String listener;

        /**
         * 消费者组名。
         */
        private String groupName;

        /**
         * 是否开启消费
         */
        private Boolean enabled;

        /**
         * 消费者订阅的主题列表。
         */
        private List<TopicInfo> topics;

        /**
         * 数据类型
         */
        private String dataType;

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
