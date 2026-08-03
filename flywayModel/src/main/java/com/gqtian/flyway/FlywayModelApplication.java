package com.gqtian.flyway;

import com.gqtian.common.config.GlobalBeautifyStart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Flyway 数据库版本迁移演示模块启动类
 * <p>
 * 启动时自动扫描 classpath:db/migration 下的脚本并按版本执行。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@SpringBootApplication
public class FlywayModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(FlywayModelApplication.class, args);
    }
}
