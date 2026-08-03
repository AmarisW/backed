-- Flyway V2：为用户表增加邮箱字段（增量变更示例）

ALTER TABLE flyway_demo_user
    ADD COLUMN email VARCHAR(128) NULL COMMENT '邮箱' AFTER nickname;
