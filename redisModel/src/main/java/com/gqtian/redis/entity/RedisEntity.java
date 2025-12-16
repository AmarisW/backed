package com.gqtian.redis.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🤨 Redis测试实体
 * @className RedisEntity
 * @author gqtian
 * @date 2025/12/16 10:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisEntity {

    // 基本数据类型
    private Long id;
    private String name;
    private Integer age;
    private Double score;
    private Float price;
    private Boolean active;
    private Character gender;
    private Byte status;
    private Short level;

    // 时间类型
    private LocalDateTime createTime;
    private LocalDate birthDate;
    private LocalTime loginTime;

    // 集合类型
    private List<String> hobbies;
    private Set<String> tags;
    private Map<String, Object> attributes;
}