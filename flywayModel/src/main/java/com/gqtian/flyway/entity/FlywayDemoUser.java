package com.gqtian.flyway.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * Flyway 演示用户表实体（对应 flyway_demo_user）
 *
 * @author gqtian
 */
@Data
@TableName("flyway_demo_user")
public class FlywayDemoUser {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 邮箱（V2 迁移新增） */
    private String email;

    /** 状态：1-启用 0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
