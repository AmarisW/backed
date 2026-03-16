create table author
(
    id           int auto_increment comment '作者ID'
        primary key,
    author_name  varchar(100) not null comment '作者姓名',
    gender       tinyint null comment '性别：0-女，1-男，2-其他',
    country      varchar(50) null comment '国籍',
    birth_date   date null comment '出生日期',
    death_date   date null comment '逝世日期',
    biography    text null comment '作者简介',
    contact_info varchar(200) null comment '联系方式或官网',
    author_photo varchar(500) null comment '作者照片URL',
    create_time  timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '作者信息表';

create index idx_author_name
    on author (author_name);

create index idx_country
    on author (country);

create table book
(
    id               int auto_increment comment '图书ID'
        primary key,
    isbn             varchar(20)  not null comment 'ISBN编号',
    title            varchar(200) not null comment '书名',
    author_id        varchar(100) not null comment '作者Id',
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
    on book (author_id);

create index idx_isbn
    on book (isbn);

create index idx_status
    on book (status);

create index idx_title
    on book (title);


create table borrow_record
(
    id                   bigint auto_increment comment '记录ID'
        primary key,
    user_id              int not null comment '用户ID',
    book_id              int not null comment '图书ID',
    borrow_time          timestamp default CURRENT_TIMESTAMP null comment '借阅时间',
    expected_return_time timestamp null comment '应还时间',
    actual_return_time   timestamp null comment '实际归还时间',
    renew_count          int       default 0 null comment '续借次数',
    status               tinyint   default 1 null comment '状态：1-借阅中，2-已归还，3-逾期，4-丢失',
    operator_id          int null comment '操作员ID（管理员ID）',
    notes                varchar(500) null comment '备注',
    create_time          timestamp default CURRENT_TIMESTAMP null comment '创建时间'
) comment '借阅记录表';

create index idx_book_id
    on borrow_record (book_id);

create index idx_borrow_time
    on borrow_record (borrow_time);

create index idx_expected_return_time
    on borrow_record (expected_return_time);

create index idx_status
    on borrow_record (status);

create index idx_user_book
    on borrow_record (user_id, book_id);

create index idx_user_id
    on borrow_record (user_id);

create table global_dict
(
    ID      varchar(128) not null comment '主键ID'
        primary key,
    VALUE   varchar(20) null comment '值',
    CODE    varchar(20) null comment '编码',
    TYPE    varchar(20) null comment '所属组',
    SORT    int null comment '排序',
    DEL_TAG varchar(2) null comment '删除标识'
) comment '全局字典表';

create table system_log
(
    log_id     bigint auto_increment comment '日志ID'
        primary key,
    user_id    int null comment '操作用户ID',
    log_type   varchar(50) null comment '日志类型：login, borrow, return, add_book等',
    operation  varchar(500) null comment '操作内容',
    ip_address varchar(50) null comment 'IP地址',
    user_agent varchar(500) null comment '用户代理',
    log_time   timestamp default CURRENT_TIMESTAMP null comment '记录时间'
) comment '系统操作日志表';

create index idx_log_time
    on system_log (log_time);

create index idx_log_type
    on system_log (log_type);

create index idx_user_id
    on system_log (user_id);