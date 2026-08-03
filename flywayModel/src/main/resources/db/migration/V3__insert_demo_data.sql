-- Flyway V3：写入演示数据

INSERT INTO flyway_demo_user (username, nickname, email, status)
VALUES ('flyway_admin', 'Flyway管理员', 'admin@flyway.demo', 1),
       ('flyway_guest', '访客', 'guest@flyway.demo', 1);
