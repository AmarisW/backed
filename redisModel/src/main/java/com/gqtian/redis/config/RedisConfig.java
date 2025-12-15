package com.gqtian.redis.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

import lombok.extern.slf4j.Slf4j;

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
     * 配置RedisTemplate模板
     * 
     * RedisTemplate是Spring Data Redis提供的用于操作Redis的模板类，
     * 它封装了对Redis的各种操作，包括对不同数据类型的增删改查。
     * 
     * 序列化配置说明：
     * - key使用StringRedisSerializer序列化，保证key为可读的字符串格式
     * - value使用Jackson2JsonRedisSerializer序列化，将Java对象转换为JSON格式存储
     * - hash的key和value都使用StringRedisSerializer序列化
     * 
     * @param factory Redis连接工厂，由Spring Boot自动配置提供
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        
        // 配置ObjectMapper用于JSON序列化
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jacksonSerial =
            new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // null值字段不显示，减少存储空间占用
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // 美化JSON输出，便于调试时查看
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类会抛出异常
        // 使用WRAPPER_ARRAY方式包装类型信息，避免类型丢失
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        // 设置value的序列化规则为JSON序列化
        template.setValueSerializer(jacksonSerial);
        
        // 设置key的序列化规则为字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        
        // 设置hash key和value的序列化规则
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        
        // 初始化template配置
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 配置Redis专用的序列化器
     * 
     * 该序列化器主要用于处理Java 8的时间类型（LocalDateTime、LocalDate、LocalTime），
     * 保证这些时间类型在Redis中能够正确地序列化和反序列化。
     * 
     * @return RedisSerializer实例，用于处理Object类型数据的序列化
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // 必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
            ObjectMapper.DefaultTyping.NON_FINAL);

        // 自定义ObjectMapper的时间处理模块，支持Java 8的时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置LocalDateTime的序列化和反序列化规则
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE_TIME)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE_TIME)));

        // 配置LocalDate的序列化和反序列化规则
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.DATE)));

        // 配置LocalTime的序列化和反序列化规则
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.TIME)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT_TYPE.TIME)));
            
        // 注册时间处理模块
        objectMapper.registerModule(javaTimeModule);

        // 禁用将日期序列化为时间戳的行为，保持可读性
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 创建并返回JSON序列化器
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 对hash类型的数据操作
     * 
     * Hash是一种键值对集合，类似于一个HashMap，适合存储对象。
     * 每个Hash可以存储2^32-1个键值对。
     * 
     * @param redisTemplate RedisTemplate实例
     * @return HashOperations实例，用于操作Redis中的Hash数据结构
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
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
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
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
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
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
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
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
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}