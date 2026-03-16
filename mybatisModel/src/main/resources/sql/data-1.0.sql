INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (1, '刘慈欣', 1, '中国', '1963-06-23', '1936-10-19', '中国科幻作家，代表作《三体》三部曲，亚洲首位获得雨果奖的作家。',
        'https://www.jkrowling.com', 'https://example.com/authors/liucixin.jpg', '2024-01-10 09:00:00',
        '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (2, 'J.K.罗琳', 0, '英国', '1965-07-31', '1936-10-19',
        '英国作家，以《哈利·波特》系列闻名于世，全球最畅销的作家之一。', 'https://www.jkrowling.com',
        'https://example.com/authors/jkrowling.jpg', '2024-01-12 14:30:00', '2026-01-05 15:43:11');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (3, '村上春树', 1, '日本', '1949-01-12', '1936-10-19',
        '日本小说家、翻译家，作品风格独特，代表作《挪威的森林》、《1Q84》。', 'https://www.jkrowling.com',
        'https://example.com/authors/murakami.jpg', '2024-01-15 10:20:00', '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (4, '余华', 1, '中国', '1960-04-03', '1936-10-19',
        '中国当代作家，代表作《活着》、《许三观卖血记》，作品被翻译成多国语言。', 'https://www.jkrowling.com',
        'https://example.com/authors/yuhua.jpg', '2024-01-18 11:45:00', '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (5, '斯蒂芬·金', 1, '美国', '1947-09-21', '1936-10-19', '美国恐怖小说大师，作品超过350部，多部被改编为影视作品。',
        'https://www.jkrowling.com', 'https://example.com/authors/stephenking.jpg', '2024-01-20 16:10:00',
        '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (6, '鲁迅', 1, '中国', '1881-09-25', '1936-10-19', '中国现代文学的奠基人，原名周树人，代表作《狂人日记》、《阿Q正传》。',
        'https://www.jkrowling.com', 'https://example.com/authors/luxun.jpg', '2024-01-22 08:50:00',
        '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (7, '玛格丽特·阿特伍德', 0, '加拿大', '1939-11-18', '1936-10-19',
        '加拿大作家、诗人，代表作《使女的故事》，布克奖得主。', 'https://www.jkrowling.com',
        'https://example.com/authors/atwood.jpg', '2024-01-25 13:25:00', '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (8, '莫言', 1, '中国', '1955-02-17', '1936-10-19', '中国作家，2012年获得诺贝尔文学奖，代表作《红高粱家族》、《蛙》。',
        'https://www.jkrowling.com', 'https://example.com/authors/moyan.jpg', '2024-01-28 15:40:00',
        '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (9, '加西亚·马尔克斯', 1, '哥伦比亚', '1927-03-06', '1936-10-19',
        '哥伦比亚作家，魔幻现实主义代表，代表作《百年孤独》，1982年诺贝尔文学奖得主。', 'https://www.jkrowling.com',
        'https://example.com/authors/marquez.jpg', '2024-02-01 10:15:00', '2026-01-05 15:43:19');
INSERT INTO mybatis_dev.author (id, author_name, gender, country, birth_date, death_date, biography, contact_info,
                                author_photo, create_time, update_time)
VALUES (10, '东野圭吾', 1, '日本', '1958-02-04', '1936-10-19',
        '日本推理小说作家，代表作《白夜行》、《嫌疑人X的献身》、《解忧杂货店》。', 'https://www.jkrowling.com',
        'https://example.com/authors/higashino.jpg', '2024-02-03 12:30:00', '2026-01-05 15:43:19');


INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (11, '9787115524670', '深入理解计算机系统', '1', '机械工业出版社', '2019-11-01', 149.00, 10, 8,
        '计算机科学区-A-001', 1, 'https://img.example.com/csapp.jpg',
        '经典的计算机系统导论教材，涵盖计算机系统所有关键概念', '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (12, '9787111563700', '代码整洁之道', '2', '人民邮电出版社', '2019-05-01', 89.00, 15, 12, '编程区-B-002', 1,
        'https://img.example.com/clean_code.jpg', '软件工程领域的经典之作，教你写出清晰、可维护的代码',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (13, '9787115484578', '算法导论', '3', '机械工业出版社', '2020-01-01', 198.00, 8, 5, '算法区-A-003', 1,
        'https://img.example.com/clrs.jpg', '算法领域的圣经，全面系统地介绍了算法设计与分析', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (14, '9787121407018', 'Java编程思想', '4', '电子工业出版社', '2021-03-01', 128.00, 20, 15, 'Java区-C-004', 1,
        'https://img.example.com/think_in_java.jpg', 'Java学习者的必读经典，深入浅出讲解Java编程思想',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (15, '9787121385491', 'Python编程：从入门到实践', '5', '人民邮电出版社', '2020-07-01', 89.00, 25, 20,
        'Python区-D-005', 1, 'https://img.example.com/python_crash.jpg', 'Python入门经典教材，理论与实践相结合',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (16, '9787121380236', '重构：改善既有代码的设计', '6', '人民邮电出版社', '2019-09-01', 99.00, 12, 9,
        '软件工程区-E-006', 1, 'https://img.example.com/refactoring.jpg', '重构领域的权威指南，教你如何改进代码设计',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (17, '9787115509813', '设计模式：可复用面向对象软件的基础', '7', '机械工业出版社', '2020-05-01', 79.00, 15, 11,
        '设计模式区-F-007', 1, 'https://img.example.com/design_patterns.jpg', 'GoF设计模式经典，软件设计的必备参考书',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (18, '9787115455639', '深入理解Java虚拟机', '8', '机械工业出版社', '2019-12-01', 129.00, 10, 6, 'Java区-C-008',
        1, 'https://img.example.com/jvm.jpg', '国内JVM领域的经典著作，深入讲解Java虚拟机原理', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (19, '9787115528109', '数据库系统概念', '9', '机械工业出版社', '2021-01-01', 159.00, 8, 5, '数据库区-G-009', 1,
        'https://img.example.com/database.jpg', '数据库领域的权威教材，全面介绍数据库系统概念', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (20, '9787121379346', '计算机网络：自顶向下方法', '10', '机械工业出版社', '2020-08-01', 89.00, 12, 8,
        '网络区-H-010', 1, 'https://img.example.com/computer_network.jpg', '计算机网络经典教材，采用自顶向下的教学方法',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (21, '9787302510383', 'Spring Boot实战', '1', '清华大学出版社', '2021-02-01', 79.00, 18, 14, '框架区-I-011', 1,
        'https://img.example.com/spring_boot.jpg', 'Spring Boot开发权威指南，快速构建企业级应用', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (22, '9787115530331', 'JavaScript高级程序设计', '2', '人民邮电出版社', '2020-11-01', 129.00, 20, 16,
        '前端区-J-012', 1, 'https://img.example.com/professional_js.jpg', '前端开发红宝书，全面深入讲解JavaScript',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (23, '9787115470668', 'Head First设计模式', '3', '中国电力出版社', '2019-06-01', 98.00, 15, 10,
        '设计模式区-F-013', 2, 'https://img.example.com/head_first_dp.jpg', '以生动有趣的方式讲解设计模式，适合初学者',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (24, '9787115522336', 'Linux内核设计与实现', '4', '机械工业出版社', '2021-04-01', 89.00, 6, 3,
        '操作系统区-K-014', 1, 'https://img.example.com/linux_kernel.jpg', '深入剖析Linux内核设计与实现原理',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (25, '9787115483477', 'C++ Primer Plus', '5', '人民邮电出版社', '2020-09-01', 118.00, 10, 7, 'C++区-L-015', 1,
        'https://img.example.com/cpp_primer.jpg', 'C++编程经典教程，适合初学者和进阶者', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (26, '9787544291170', '百年孤独', '6', '南海出版公司', '2017-08-01', 55.00, 15, 10, '外国文学区-M-016', 1,
        'https://img.example.com/one_hundred_years.jpg', '魔幻现实主义文学代表作，讲述布恩迪亚家族七代人的传奇故事',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (27, '9787020002207', '围城', '7', '人民文学出版社', '2018-05-01', 39.00, 20, 15, '中国现代文学区-N-017', 1,
        'https://img.example.com/fortress_besieged.jpg', '中国现代文学经典，深刻揭示人性的困境', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (28, '9787020008728', '红楼梦', '8', '人民文学出版社', '2019-01-01', 59.00, 12, 8, '古典文学区-O-018', 1,
        'https://img.example.com/dream_of_red_mansion.jpg', '中国古典小说巅峰之作，四大名著之首', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (29, '9787020156351', '活着', '9', '作家出版社', '2017-06-01', 28.00, 25, 18, '当代文学区-P-019', 2,
        'https://img.example.com/to_live.jpg', '余华代表作，讲述普通人在历史洪流中的挣扎与生存', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (30, '9787536692930', '三体', '10', '重庆出版社', '2018-07-01', 93.00, 18, 12, '科幻区-Q-020', 1,
        'https://img.example.com/three_body.jpg', '中国科幻里程碑作品，雨果奖获奖作品', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (31, '9787544258609', '白夜行', '1', '南海出版公司', '2017-08-01', 59.60, 15, 9, '推理小说区-R-021', 1,
        'https://img.example.com/into_the_white_night.jpg', '东野圭吾代表作，讲述两个孩子的悲剧命运',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (32, '9787544285148', '解忧杂货店', '2', '南海出版公司', '2020-01-01', 45.00, 22, 17, '治愈系文学区-S-022', 1,
        'https://img.example.com/miracles_of_namiya.jpg', '温暖人心的治愈系小说，讲述跨越时空的温情故事',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (33, '9787020167098', '平凡的世界', '3', '北京十月文艺出版社', '2020-03-01', 138.00, 10, 6,
        '现实主义文学区-T-023', 1, 'https://img.example.com/ordinary_world.jpg',
        '路遥代表作，展现中国社会变迁的壮丽画卷', '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (34, '9787530216784', '嫌疑人X的献身', '4', '南海出版公司', '2019-05-01', 35.00, 16, 11, '推理小说区-R-024', 1,
        'https://img.example.com/suspect_x.jpg', '东野圭吾推理小说巅峰之作，极致爱情与推理的结合', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (35, '9787544291163', '霍乱时期的爱情', '5', '南海出版公司', '2020-02-01', 49.50, 14, 9, '外国文学区-M-025', 1,
        'https://img.example.com/love_in_cholera.jpg', '跨越半个世纪的爱情史诗，展现爱情的所有可能性',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (36, '9787544281102', '追风筝的人', '6', '上海人民出版社', '2018-09-01', 36.00, 20, 14, '外国文学区-M-026', 1,
        'https://img.example.com/kite_runner.jpg', '感动全球千万读者的成长小说，讲述友谊与救赎', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (37, '9787020008729', '西游记', '7', '人民文学出版社', '2019-03-01', 68.00, 8, 5, '古典文学区-O-027', 1,
        'https://img.example.com/journey_to_the_west.jpg', '中国古典神魔小说，四大名著之一', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (38, '9787020125265', '呐喊', '8', '人民文学出版社', '2018-10-01', 25.00, 15, 10, '中国现代文学区-N-028', 1,
        'https://img.example.com/call_to_arms.jpg', '鲁迅小说集，中国现代文学的开山之作', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (39, '9787020056309', '骆驼祥子', '9', '人民文学出版社', '2019-04-01', 29.00, 12, 7, '中国现代文学区-N-029', 1,
        'https://img.example.com/camel_xiangzi.jpg', '老舍代表作，描绘旧社会人力车夫的悲惨命运', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (40, '9787533947676', '人生海海', '10', '北京十月文艺出版社', '2021-06-01', 55.00, 18, 13, '当代文学区-P-030', 2,
        'https://img.example.com/life_like_sea.jpg', '麦家时隔八年又一力作，讲述一个传奇英雄的故事',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (41, '9787111635573', '经济学原理', '1', '机械工业出版社', '2020-05-01', 128.00, 10, 7, '经济学区-U-031', 1,
        'https://img.example.com/principles_of_economics.jpg', '经典经济学教材，深入浅出讲解经济学基本原理',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (42, '9787508689589', '穷查理宝典', '2', '中信出版社', '2019-08-01', 168.00, 8, 4, '投资理财区-V-032', 1,
        'https://img.example.com/poor_charlie.jpg', '查理·芒格的智慧箴言录，投资与人生的思考', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (43, '9787508687219', '原则', '3', '中信出版社', '2018-01-01', 98.00, 12, 8, '管理学区-W-033', 1,
        'https://img.example.com/principles.jpg', '桥水基金创始人分享生活和工作的原则', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (44, '9787508672062', '思考，快与慢', '4', '中信出版社', '2018-09-01', 69.00, 15, 10, '心理学区-X-034', 1,
        'https://img.example.com/thinking_fast_and_slow.jpg', '诺贝尔经济学奖得主揭示人类思维的秘密',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (45, '9787508677517', '影响力', '5', '中国人民大学出版社', '2020-03-01', 58.00, 18, 13, '心理学区-X-035', 1,
        'https://img.example.com/influence.jpg', '社会心理学经典，揭示说服他人的六大原则', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (46, '9787111633944', '管理学', '6', '中国人民大学出版社', '2021-01-01', 79.00, 10, 6, '管理学区-W-036', 1,
        'https://img.example.com/management.jpg', '管理学经典教材，系统讲解管理学理论和方法', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (47, '9787508683969', '定位', '7', '机械工业出版社', '2019-11-01', 59.00, 12, 8, '营销学区-Y-037', 1,
        'https://img.example.com/positioning.jpg', '营销经典，讲述如何在潜在顾客心智中占据有利位置',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (48, '9787508692091', '精益创业', '8', '中信出版社', '2020-07-01', 49.00, 14, 9, '创业区-Z-038', 1,
        'https://img.example.com/lean_startup.jpg', '互联网时代创业方法论，快速验证商业假设', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (49, '9787508683228', '从0到1', '9', '中信出版社', '2019-02-01', 45.00, 16, 11, '创业区-Z-039', 1,
        'https://img.example.com/zero_to_one.jpg', 'PayPal创始人分享创业心得，开启商业与未来的秘密',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (50, '9787508652439', '黑天鹅', '10', '中信出版社', '2019-04-01', 68.00, 10, 6, '经济学区-U-040', 1,
        'https://img.example.com/black_swan.jpg', '揭示极端事件的重要影响，改变人们看待世界的方式',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (51, '9787108041531', '史记', '1', '中华书局', '2019-05-01', 198.00, 6, 3, '历史区-AA-041', 1,
        'https://img.example.com/records_of_the_historian.jpg', '中国第一部纪传体通史，二十四史之首',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (52, '9787101110988', '资治通鉴', '2', '中华书局', '2020-08-01', 298.00, 4, 2, '历史区-AA-042', 1,
        'https://img.example.com/mirror_of_government.jpg', '中国第一部编年体通史，涵盖16朝1362年历史',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (53, '9787108063106', '中国通史', '3', '中华书局', '2021-02-01', 88.00, 10, 7, '历史区-AA-043', 1,
        'https://img.example.com/general_history_of_china.jpg', '吕思勉先生的经典通史著作，深入浅出',
        '2025-12-29 17:51:19', '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (54, '9787532767373', '苏菲的世界', '4', '作家出版社', '2019-07-01', 36.00, 15, 11, '哲学区-BB-044', 1,
        'https://img.example.com/sophies_world.jpg', '哲学入门小说，以故事形式介绍西方哲学史', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (55, '9787108044020', '论语', '5', '中华书局', '2018-11-01', 28.00, 20, 15, '哲学区-BB-045', 1,
        'https://img.example.com/analects.jpg', '儒家经典，记录孔子及其弟子言行的语录集', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (56, '9787101113668', '道德经', '6', '中华书局', '2019-12-01', 25.00, 18, 13, '哲学区-BB-046', 2,
        'https://img.example.com/tao_te_ching.jpg', '道家哲学经典，中国哲学的重要源头', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (57, '9787108061478', '理想国', '7', '商务印书馆', '2020-06-01', 48.00, 8, 4, '哲学区-BB-047', 1,
        'https://img.example.com/republic.jpg', '柏拉图代表作，西方政治哲学的源头', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (58, '9787108067555', '社会契约论', '8', '商务印书馆', '2019-09-01', 32.00, 10, 6, '哲学区-BB-048', 1,
        'https://img.example.com/social_contract.jpg', '政治哲学经典，探讨政治权力的合法性基础', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (59, '9787532772322', '人类简史', '9', '中信出版社', '2020-04-01', 68.00, 16, 11, '历史区-AA-049', 1,
        'https://img.example.com/sapiens.jpg', '从石器时代到21世纪的人类发展史', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');
INSERT INTO mybatis_dev.book (id, isbn, title, author_id, publisher, publish_date, price, total_copies,
                              available_copies, location, status, cover_image, description, create_time, update_time)
VALUES (60, '9787508684157', '未来简史', '10', '中信出版社', '2021-01-01', 68.00, 14, 9, '未来学区-CC-050', 1,
        'https://img.example.com/homo_deus.jpg', '探讨人类未来的可能命运和挑战', '2025-12-29 17:51:19',
        '2026-01-05 15:24:05');


INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (1, 1001, 11, '2024-03-10 09:30:00', '2024-04-10 09:30:00', '2024-04-09 14:20:00', 1, 2, 1, '按时归还，无破损',
        '2024-03-10 09:30:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (2, 1002, 12, '2024-03-12 14:15:00', '2024-04-12 14:15:00', '2024-04-09 14:20:00', 0, 3, 2,
        '逾期未还，已发送催还通知', '2024-03-12 14:15:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (3, 1003, 13, '2024-03-15 10:00:00', '2024-04-15 10:00:00', '2024-04-09 14:20:00', 0, 2, 1, '提前归还，状态良好',
        '2024-03-15 10:00:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (4, 1004, 14, '2024-03-18 16:45:00', '2024-04-18 16:45:00', '2024-04-09 14:20:00', 2, 1, 3,
        '已续借两次，正常借阅中', '2024-03-18 16:45:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (5, 1005, 11, '2024-03-20 11:20:00', '2024-04-20 11:20:00', '2024-04-09 14:20:00', 0, 4, 2,
        '图书丢失，用户已报失', '2024-03-20 11:20:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (6, 1006, 12, '2024-03-22 13:10:00', '2024-04-22 13:10:00', '2024-04-09 14:20:00', 1, 2, 1,
        '逾期3天归还，缴纳罚金', '2024-03-22 13:10:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (7, 1007, 13, '2024-03-28 09:45:00', '2024-04-28 09:45:00', '2024-04-09 14:20:00', 0, 2, 2, '准时归还，无异常',
        '2024-03-28 09:45:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (8, 1008, 14, '2024-04-01 14:00:00', '2024-05-01 14:00:00', '2024-04-09 14:20:00', 1, 1, 1, '已续借一次，借阅中',
        '2024-04-01 14:00:00');
INSERT INTO mybatis_dev.borrow_record (id, user_id, book_id, borrow_time, expected_return_time, actual_return_time,
                                       renew_count, status, operator_id, notes, create_time)
VALUES (9, 1009, 11, '2024-04-03 16:20:00', '2024-05-03 16:20:00', '2024-04-09 14:20:00', 0, 2, 3,
        '按时归还，图书有轻微折痕', '2024-04-03 16:20:00');
