package com.gqtian.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.gqtian.common.constant.GlobalConstant.DATE_FORMAT_TYPE;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * 
 * 该配置类主要完成以下功能：
 * 1. 配置RedisTemplate，用于操作Redis数据库
 * 2. 配置RedisSerializer，用于序列化和反序列化Redis中的数据
 * 3. 提供各种Redis数据结构的操作Bean
 * 
 * @author gqtian
 * @date 2025/12/10 10:54
 */
@Slf4j
@Configuration
public class RedisConfig {


    /**
     * 创建RedisTemplate实例
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 创建ObjectMapper用于序列化/反序列化Java对象
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置所有属性可见，允许访问所有字段（包括private）
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 启用默认的类型信息：将对象类型信息包含在JSON中，便于反序列化时恢复原始对象类型
        // LaissezFaireSubTypeValidator.instance - 允许任何子类型（较为宽松的验证策略）
        // NON_FINAL - 只为非final类的属性添加类型信息
        // WRAPPER_ARRAY - 以数组包装形式存储类型信息，格式为: ["class.path", {...data...}]
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        // 注册JavaTimeModule以支持Java 8时间类型（LocalDateTime、LocalDate、LocalTime）的序列化/反序列化
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置LocalDateTime的序列化器和反序列化器，使用全局时间格式常量
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE_TIME)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE_TIME)));

        // 配置LocalDate的序列化器和反序列化器
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE)));

        // 配置LocalTime的序列化器和反序列化器
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.TIME)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.TIME)));

        // 将时间模块注册到ObjectMapper中
        objectMapper.registerModule(javaTimeModule);

        // 禁用将日期序列化为时间戳的默认行为，保持日期的可读性格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置序列化时忽略null值字段，减少存储空间占用
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 创建Jackson序列化器，使用上述配置的ObjectMapper进行JSON序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
            objectMapper, Object.class);
        // 创建字符串序列化器，用于Redis键的序列化（保持为字符串格式）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 配置RedisTemplate的序列化策略
        // Key使用StringRedisSerializer，保证key为可读的字符串格式
        template.setKeySerializer(stringRedisSerializer);
        // Value使用Jackson2JsonRedisSerializer，将Java对象转换为JSON格式存储
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash的Key也使用字符串序列化
        template.setHashKeySerializer(stringRedisSerializer);
        // Hash的Value使用JSON序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化RedisTemplate，设置上述配置后必须调用此方法以生效
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 对hash类型的数据操作
     *
     * Hash是一种键值对集合，类似于Java中的HashMap，特残适合存储带有多个属性的对象。
     * 需要注意的是，Hash类型不支持整个对象的序列化，辙只有流式序列化的效果。
     * 每个Hash可以存储2^32-1个键值对。
     *
     * @param redisTemplate RedisTemplate实例，妹绑已配置好的RedisTemplate
     * @return HashOperations实例，下游代码可直接注入使用
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     *
     * String是Redis最基本的数据类型，一个key对应一个value。
     * String类型是二进制安全的，可以包含任何数据。
     *
     * @param redisTemplate RedisTemplate实例
     * @return ValueOperations实例，用于操作Redis中的String数据结构
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * List是简单的字符串列表，按照插入顺序排序。
     * 可以在头部（左边）和尾部（右边）添加元素。
     *
     * @param redisTemplate RedisTemplate实例
     * @return ListOperations实例，用于操作Redis中的List数据结构
     */
    @Bean
    public ListOperations<String, Object> listOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * Set是String类型的无序集合，通过哈希表实现，添加、删除、查找的复杂度都是O(1)。
     * 集合内不允许有重复的数据。
     *
     * @param redisTemplate RedisTemplate实例
     * @return SetOperations实例，用于操作Redis中的Set数据结构
     */
    @Bean
    public SetOperations<String, Object> setOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * ZSet和Set一样也是String类型元素的集合，且不允许重复的成员。
     * 不同的是每个元素都会关联一个double类型的分数(score)。
     * Redis通过分数来为集合中的成员进行从小到大的排序。
     *
     * @param redisTemplate RedisTemplate实例
     * @return ZSetOperations实例，用于操作Redis中的ZSet数据结构
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(
        RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}