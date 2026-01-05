package com.gqtian.mybatis.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 系统操作日志表
 * @TableName system_logs
 */
@TableName(value = "system_log")
@Data
public class SystemLog {
    /**
     * 日志ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 日志类型：login, borrow, return, add_book等
     */
    private String logType;

    /**
     * 操作内容
     */
    private String operation;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 记录时间
     */
    private Date logTime;
}