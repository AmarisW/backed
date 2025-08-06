package com.gqtian.common.config;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 启动工具
 *
 * @author gqtian
 * @className LaunchUtil
 * @date 2024/4/8 13:53
 */
@Slf4j
public class LaunchApplicationContext {
    
    public static final String SERVER_PORT = "server.port";
    
    private static final String SERVER_SERVLET_CONTEXT_PATH_ = "server.servlet.context-path";
    
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";
    
    private static final String SPRING_APPLICATION_VERSION = "spring.application.version";
    
    /**
     * 启动
     */
    public static <T> void launch(T inClass, String[] args) {
        // 将startTime移到方法开始处，确保计算完整的启动时间
        Instant startTime = Instant.now();
        // 启动应用
        ConfigurableApplicationContext application = SpringApplication.run((Class<?>)inClass, args);
        // 输出启动信息
        doOutLog(application, startTime);
    }
    
    /**
     * 输出启动信息
     * 
     * @param application 上下文
     * @param startTime 上下文启动时间
     */
    private static void doOutLog(ConfigurableApplicationContext application, Instant startTime) {
        try {
            Environment environment = application.getEnvironment();
            String ip = InetAddress.getLocalHost().getHostAddress();
            String port = environment.getProperty(SERVER_PORT);
            String property = environment.getProperty(SERVER_SERVLET_CONTEXT_PATH_);
            String path = property == null ? "" : property;
            String appName = environment.getProperty(SPRING_APPLICATION_NAME, "未配置项目名");
            String appVersion = environment.getProperty(SPRING_APPLICATION_VERSION, "1.0.0");
            
            // 构建输出信息
            StringBuilder output = new StringBuilder();
            
            // 使用banner.txt中的图案，增加更多彩色装饰
            output.append("\n");
            output.append("\u001B[35m╭───────────────────────────────\u001B[91m ⭐ \u001B[33mApplication " + "Starting! "
                + "\u001B[91m ⭐ \u001B[35m───────────────────────────────╮\u001B[0m\n");
            output.append("""
                \u001B[36m \u001B[0m
                \u001B[36m 　  ┏┓　 ┏┓\u001B[91m+ +\u001B[0m
                \u001B[36m 　┏┛┻━━━┛┻┓\u001B[91m + +\u001B[0m
                \u001B[36m 　┃　　　　　　 ┃\u001B[93m 　\u001B[0m
                \u001B[36m 　┃　　　━　　　┃\u001B[91m ++ + + +\u001B[0m
                \u001B[36m \u001B[91m ████━████\u001B[36m ┃\u001B[91m+\u001B[0m
                \u001B[36m 　┃　　　　　　 ┃\u001B[91m +\u001B[0m
                \u001B[36m 　┃　　　┻　　　┃\u001B[0m
                \u001B[36m 　┃　　　　　　 ┃\u001B[91m + +\u001B[0m
                \u001B[36m 　┗━┓　　　┏━┛\u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[93m　　　　　　　　　　　\u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[91m + + + +\u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[91m +  \u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[91m    \u001B[0m
                \u001B[36m 　　　┃　　　┃\u001B[91m　　+　　　　　　　　　\u001B[0m
                \u001B[36m 　　　┃　 　　┗━━━┓\u001B[91m + +\u001B[0m
                \u001B[36m 　　　┃\u001B[95m 　　　　　　　┣┓\u001B[36m  \u001B[0m
                \u001B[36m 　　　┃\u001B[95m 　　　　　　　┏┛\u001B[36m x x x x x x x x x x x x x x x x x x\u001B[0m
                \u001B[36m 　　　┗┓┓┏━┳┓┏┛\u001B[91m + + + +\u001B[0m
                \u001B[36m 　　　　┃┫┫　┃┫┫\u001B[0m
                \u001B[36m 　　　　┗┻┛　┗┻┛\u001B[91m+ + + +\u001B[0m
                \u001B[36m \u001B[0m
                """);
            
            output.append("\u001B[92m──────────────────────────────────────────────────────────────\u001B[0m\n");
            output.append("\n");
            // 配置信息
            String[] activeProfiles = environment.getActiveProfiles();
            if (activeProfiles.length > 0) {
                output.append("\u001B[1m\u001B[94mActive Profiles:\u001B[0m\n");
                output.append("\u001B[95m└─ \u001B[0m");
                for (int i = 0; i < activeProfiles.length; i++) {
                    output.append("\u001B[93m").append(activeProfiles[i]).append("\u001B[0m");
                    if (i < activeProfiles.length - 1) {
                        output.append("\u001B[97m, \u001B[0m");
                    }
                }
                output.append("\n");
            }
            
            // 应用基本信息
            output.append("\n\u001B[1m\u001B[94mApplication Information:\u001B[0m\n");
            output.append("\u001B[95m├─ \u001B[0m\u001B[97mName:    \u001B[0m")
                .append("\u001B[92m")
                .append(appName)
                .append("\u001B[0m\n");
            output.append("\u001B[95m├─ \u001B[0m\u001B[97mVersion: \u001B[0m")
                .append("\u001B[92m")
                .append(appVersion)
                .append("\u001B[0m\n");
            output.append("\u001B[95m├─ \u001B[0m\u001B[97mPort:    \u001B[0m")
                .append("\u001B[92m")
                .append(port)
                .append("\u001B[0m\n");
            output.append("\u001B[95m└─ \u001B[0m\u001B[97mContext: \u001B[0m")
                .append("\u001B[92m")
                .append(path.isEmpty() ? "/" : path)
                .append("\u001B[0m\n");
            
            output.append("\n");
            
            // 访问地址信息
            output.append("\u001B[1m\u001B[94mAccess URLs:\u001B[0m\n");
            output.append("\u001B[95m├─ \u001B[0m\u001B[97mLocal:   \u001B[0m")
                .append("\u001B[96mhttp://localhost:")
                .append(port)
                .append(path)
                .append("/\u001B[0m\n");
            output.append("\u001B[95m└─ \u001B[0m\u001B[97mExternal:\u001B[0m ")
                .append("\u001B[96mhttp://")
                .append(ip)
                .append(":")
                .append(port)
                .append(path)
                .append("/\u001B[0m\n");
            output.append("\n");
            
            // 启动耗时统计
            Duration duration = Duration.between(startTime, Instant.now());
            long seconds = duration.getSeconds();
            long millis = duration.toMillisPart();
            
            // 增加更多装饰性小火箭
            output.append("\u001B[92m🚀  Application started successfully! \u001B[0m\n");
            output.append("\u001B[96m⏱️  Startup Time: \u001B[0m")
                .append("\u001B[93m")
                .append(seconds)
                .append("s ")
                .append(millis)
                .append("ms\u001B[0m\n");
            output.append("\u001B[35m╰───────────────────────────────\u001B[91m ⭐ " + "\u001B[33mApplication "
                + "Running! " + "\u001B[91m ⭐ \u001B[35m───────────────────────────────╯\u001B[0m\n");
            
            // 一次性输出所有信息
            log.info("{}", output);
            
        }
        catch (Exception e) {
            log.error("启动失败", e);
        }
    }
    
}