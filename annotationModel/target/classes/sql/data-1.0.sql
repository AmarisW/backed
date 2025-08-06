#  模拟数据
CREATE TEMPORARY TABLE temp_names
(
    name VARCHAR(50)
);

INSERT INTO temp_names (name)
VALUES ('白庆祝'),
       ('无名氏'),
       ('王小明'),
       ('李小红'),
       ('张三'),
       ('李四'),
       ('王五'),
       ('赵六'),
       ('钱七'),
       ('孙八');

INSERT INTO ex_user (ID, NAME, AGE, BIRTHDAY)
SELECT FLOOR(RAND() * 100) + 1,
       name,
       FLOOR(RAND() * 100) + 1,
       DATE_ADD('2024-04-08 14:59:19', INTERVAL FLOOR(RAND() * 100) DAY)
FROM temp_names
ORDER BY RAND()
LIMIT 100;

DROP TABLE temp_names;
