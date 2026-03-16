create table book_slave
(
    id               int auto_increment comment '图书ID'
        primary key,
    isbn             varchar(20)  not null comment 'ISBN编号',
    title            varchar(200) not null comment '书名',
    author           varchar(100) not null comment '作者',
    publisher        varchar(100) null comment '出版社',
    publish_date     date null comment '出版日期',
    price            decimal(10, 2) null comment '价格',
    total_copies     int       default 0 null comment '总库存数量',
    available_copies int       default 0 null comment '可借数量',
    location         varchar(100) null comment '馆藏位置',
    status           tinyint   default 1 null comment '状态：1-可借，2-已借出，3-维修中，4-已下架',
    cover_image      varchar(500) null comment '封面图片URL',
    description      text null comment '图书描述',
    create_time      timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint isbn
        unique (isbn)
) comment '图书信息表';

create index idx_author
    on book_slave (author);

create index idx_isbn
    on book_slave (isbn);

create index idx_status
    on book_slave (status);

create index idx_title
    on book_slave (title);

create table global_dict
(
    ID      varchar(128) not null comment '主键ID'
        primary key,
    VALUE   varchar(20) null comment '值',
    CODE    varchar(20) null comment '编码',
    TYPE    varchar(20) null comment '所属组',
    SORT    int null comment '排序',
    DEL_TAG varchar(2) null comment '删除标识'
) comment '全局字典表' row_format = DYNAMIC;

create table system_user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)  not null comment '用户名',
    password    varchar(100) not null comment '密码(建议存储加密后的值)',
    nickname    varchar(50) null comment '用户昵称',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '系统用户表' row_format = DYNAMIC;