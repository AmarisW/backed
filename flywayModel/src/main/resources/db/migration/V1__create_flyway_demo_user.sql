-- Flyway V1：创建演示用户表
-- 说明：版本号 + 双下划线 + 描述，文件名不可随意改动（已执行后会被校验）

CREATE TABLE IF NOT EXISTS flyway_demo_user
(
    id          BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL COMMENT '用户名',
    nickname    VARCHAR(64)  NULL COMMENT '昵称',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) COMMENT 'Flyway 演示用户表';
