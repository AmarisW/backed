package com.gqtian.rabbit;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RabbitMQ 学习模块启动类。
 * <p>
 * 排除 {@link GlobalDatabaseAutoConfiguration}：本模块不依赖数据源，
 * 避免公共自动配置强拉数据库导致启动失败。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@SpringBootApplication(exclude = {GlobalDatabaseAutoConfiguration.class})
public class RabbitMqModelApplication {

    /**
     * 模块入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        GlobalBeautifyStart.start(RabbitMqModelApplication.class, args);
    }
}
