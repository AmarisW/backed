-- Easy-Trans 学习模块初始化脚本
CREATE DATABASE IF NOT EXISTS easy_trans DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE easy_trans;

-- 部门表（SIMPLE 翻译数据源）
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept
(
    id        BIGINT       NOT NULL COMMENT '部门ID',
    dept_name VARCHAR(64)  NOT NULL COMMENT '部门名称',
    deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
    PRIMARY KEY (id)
) COMMENT '部门表';

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id        BIGINT       NOT NULL COMMENT '用户ID',
    username  VARCHAR(64)  NOT NULL COMMENT '登录名',
    real_name VARCHAR(64)  NOT NULL COMMENT '真实姓名',
    dept_id   BIGINT       NOT NULL COMMENT '部门ID',
    gender    VARCHAR(8)   NOT NULL COMMENT '性别字典码 0-男 1-女',
    status    TINYINT      NOT NULL COMMENT '状态 1-正常 2-禁用',
    create_by BIGINT                COMMENT '创建人ID',
    deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
    PRIMARY KEY (id)
) COMMENT '用户表';

-- 字典表（DICTIONARY 翻译数据源）
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict
(
    id         BIGINT       NOT NULL COMMENT '字典ID',
    dict_type  VARCHAR(64)  NOT NULL COMMENT '字典类型',
    dict_code  VARCHAR(64)  NOT NULL COMMENT '字典编码',
    dict_label VARCHAR(128) NOT NULL COMMENT '字典标签',
    deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
    PRIMARY KEY (id)
) COMMENT '字典表';

INSERT INTO sys_dept (id, dept_name, deleted) VALUES
(1, '研发部', 0),
(2, '产品部', 0),
(3, '市场部', 0);

INSERT INTO sys_dict (id, dict_type, dict_code, dict_label, deleted) VALUES
(1, 'gender', '0', '男', 0),
(2, 'gender', '1', '女', 0);

INSERT INTO sys_user (id, username, real_name, dept_id, gender, status, create_by, deleted) VALUES
(1, 'admin', '管理员', 1, '0', 1, NULL, 0),
(2, 'zhangsan', '张三', 1, '0', 1, 1, 0),
(3, 'lisi', '李四', 2, '1', 1, 1, 0),
(4, 'wangwu', '王五', 3, '0', 2, 2, 0);
