package com.gqtian.mongodb.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB 用户文档实体（集合：user）
 * <p>
 * 用于演示文档 CRUD、索引、嵌套字段与数组操作。
 * </p>
 *
 * @author gqtian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class UserDocument {

    /**
     * MongoDB 主键（ObjectId 字符串）
     */
    @Id
    private String id;

    /**
     * 用户名（唯一索引）
     */
    @Indexed(unique = true)
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 标签列表（演示数组字段）
     */
    private List<String> tags;

    /**
     * 扩展信息（演示嵌套文档）
     */
    private Profile profile;

    /**
     * 创建时间
     */
    @Field("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field("update_time")
    private LocalDateTime updateTime;

    /**
     * 嵌套个人资料
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        /** 城市 */
        private String city;
        /** 简介 */
        private String bio;
    }
}
