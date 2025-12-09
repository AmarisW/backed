-- 初始化用户表
create table system_user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)  not null comment '用户名',
    password    varchar(100) not null comment '密码(建议存储加密后的值)',
    nickname    varchar(50) null comment '用户昵称',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint idx_username
        unique (username) comment '用户名唯一索引'
) comment '系统用户表' row_format = DYNAMIC;