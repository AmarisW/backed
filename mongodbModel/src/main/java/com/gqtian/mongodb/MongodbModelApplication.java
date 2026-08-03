package com.gqtian.mongodb;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MongoDB 对接测试模块启动类
 * <p>
 * 本模块不依赖关系型数据库，排除公共 MySQL 数据源自动配置。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@SpringBootApplication(exclude = {GlobalDatabaseAutoConfiguration.class})
public class MongodbModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(MongodbModelApplication.class, args);
    }
}
