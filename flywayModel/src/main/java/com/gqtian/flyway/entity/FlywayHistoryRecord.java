package com.gqtian.flyway.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * Flyway 迁移历史实体（对应 flyway_schema_history）
 *
 * @author gqtian
 */
@Data
@TableName("flyway_schema_history")
public class FlywayHistoryRecord {

    /** 安装顺序（主键） */
    @TableId("installed_rank")
    private Integer installedRank;

    /** 版本号，如 1、2、3 */
    private String version;

    /** 脚本描述 */
    private String description;

    /** 脚本类型：SQL / JDBC 等 */
    private String type;

    /** 脚本文件名 */
    private String script;

    /** 校验和 */
    private Integer checksum;

    /** 执行人 */
    private String installedBy;

    /** 执行时间 */
    private LocalDateTime installedOn;

    /** 耗时（毫秒） */
    private Integer executionTime;

    /** 是否成功 */
    private Boolean success;
}
