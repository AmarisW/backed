package com.gqtian.common.config;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.gqtian.common.model.GlobalApiOutEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 启动工具
 *
 * @author gqtian
 * @className LaunchUtil
 * @date 2024/4/8 13:53
 */
@Slf4j
public class GlobalBeautifyStart {
    
    private static final String SERVER_PORT = "server.port";
    
    private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
    
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";
    
    private static final String SPRING_APPLICATION_VERSION = "spring.application.version";
    
    // 彩色输出常量
    private static final String COLOR_PREFIX = "\u001B[";
    
    private static final String COLOR_RESET = COLOR_PREFIX + "0m";
    
    private static final String COLOR_MAGENTA = COLOR_PREFIX + "35m";
    
    private static final String COLOR_CYAN = COLOR_PREFIX + "36m";
    
    private static final String COLOR_BLUE = COLOR_PREFIX + "94m";
    
    private static final String COLOR_GREEN = COLOR_PREFIX + "92m";
    
    private static final String COLOR_YELLOW = COLOR_PREFIX + "93m";
    
    private static final String COLOR_WHITE = COLOR_PREFIX + "97m";
    
    private static final String COLOR_PINK = COLOR_PREFIX + "95m";
    
    private static final String COLOR_RED = COLOR_PREFIX + "91m";
    
    private static final String COLOR_ORANGE = COLOR_PREFIX + "33m";
    
    private static final String COLOR_BOLD = COLOR_PREFIX + "1m";
    
    // 符号常量
    private static final String TREE_LAST_NODE = COLOR_PINK + "└─ " + COLOR_RESET;
    
    private static final String TREE_NODE = COLOR_PINK + "├─ " + COLOR_RESET;
    
    private static final String TREE_BAR = COLOR_PINK + "│  " + COLOR_RESET;
    
    /**
     * 开放接口请求
     */
    public final static LinkedBlockingQueue<GlobalApiOutEntity> GLOBAL_API_CACHE = new LinkedBlockingQueue<>();
    
    /**
     * 启动
     */
    public static <T> void start(T inClass, String[] args) {
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
            String contextPath = environment.getProperty(SERVER_SERVLET_CONTEXT_PATH);
            String path = contextPath == null ? "" : contextPath;
            String appName = environment.getProperty(SPRING_APPLICATION_NAME, "未配置项目名");
            String appVersion = environment.getProperty(SPRING_APPLICATION_VERSION, "1.0.0");
            
            // 构建输出信息
            StringBuilder output = new StringBuilder();
            
            // 使用banner.txt中的图案，增加更多彩色装饰
            appendBanner(output);
            
            // 配置信息
            appendActiveProfiles(output, environment);
            
            // 应用基本信息
            appendApplicationInfo(output, appName, appVersion, port, path);
            
            // 访问地址信息
            appendAccessUrls(output, port, path, ip);
            
            // 输出开放接口属性
            appendExposedApis(output);
            
            // 启动耗时统计
            appendStartupTime(output, startTime);
            
            // 一次性输出所有信息
            log.info("{}", output);
            
        }
        catch (Exception e) {
            log.error("启动失败", e);
        }
    }
    
    /**
     * 添加启动横幅
     */
    private static void appendBanner(StringBuilder output) {
        output.append("\n")
            .append(COLOR_MAGENTA)
            .append("╭───────────────────────────────")
            .append(COLOR_RED)
            .append(" ⭐ ")
            .append(COLOR_ORANGE)
            .append("Application Starting! ")
            .append(COLOR_RED)
            .append(" ⭐ ")
            .append(COLOR_MAGENTA)
            .append("───────────────────────────────╮")
            .append(COLOR_RESET)
            .append("\n");
        
        output.append(COLOR_CYAN)
            .append(" \u001B[0m\n")
            .append(COLOR_CYAN)
            .append(" 　  ┏┓　 ┏┓")
            .append(COLOR_RED)
            .append("+ +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┏┛┻━━━┛┻┓")
            .append(COLOR_RED)
            .append(" + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┃　　　　　　 ┃")
            .append(COLOR_YELLOW)
            .append(" 　")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┃　　　━　　　┃")
            .append(COLOR_RED)
            .append(" ++ + + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_RED)
            .append(" ████━████")
            .append(COLOR_CYAN)
            .append(" ┃")
            .append(COLOR_RED)
            .append("+")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┃　　　　　　 ┃")
            .append(COLOR_RED)
            .append(" +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┃　　　┻　　　┃")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┃　　　　　　 ┃")
            .append(COLOR_RED)
            .append(" + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　┗━┓　　　┏━┛")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_YELLOW)
            .append("　　　　　　　　　　　")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_RED)
            .append(" + + + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_RED)
            .append(" +  ")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_RED)
            .append("    ")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　　　┃")
            .append(COLOR_RED)
            .append("　　+　　　　　　　　　")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃　 　　┗━━━┓")
            .append(COLOR_RED)
            .append(" + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃")
            .append(COLOR_PINK)
            .append(" 　　　　　　　┣┓")
            .append(COLOR_CYAN)
            .append("  ")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┃")
            .append(COLOR_PINK)
            .append(" 　　　　　　　┏┛")
            .append(COLOR_CYAN)
            .append(" x x x x x x x x x x x x x x x x x x")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　┗┓┓┏━┳┓┏┛")
            .append(COLOR_RED)
            .append(" + + + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　　┃┫┫　┃┫┫")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" 　　　　┗┻┛　┗┻┛")
            .append(COLOR_RED)
            .append("+ + + +")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append(" \u001B[0m\n");
        
        output.append(COLOR_GREEN)
            .append("──────────────────────────────────────────────────────────────")
            .append(COLOR_RESET)
            .append("\n\n");
    }
    
    /**
     * 添加激活的配置信息
     */
    private static void appendActiveProfiles(StringBuilder output, Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            output.append(COLOR_BOLD)
                .append(COLOR_BLUE)
                .append("Active Profiles:")
                .append(COLOR_RESET)
                .append("\n")
                .append(TREE_LAST_NODE);
            
            for (int i = 0; i < activeProfiles.length; i++) {
                output.append(COLOR_YELLOW).append(activeProfiles[i]).append(COLOR_RESET);
                
                if (i < activeProfiles.length - 1) {
                    output.append(COLOR_WHITE).append(", ").append(COLOR_RESET);
                }
            }
            output.append("\n");
        }
    }
    
    /**
     * 添加应用基本信息
     */
    private static void appendApplicationInfo(StringBuilder output, String appName, String appVersion, String port,
        String path) {
        output.append("\n")
            .append(COLOR_BOLD)
            .append(COLOR_BLUE)
            .append("Application Information:")
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_NODE)
            .append(COLOR_WHITE)
            .append("Name:    ")
            .append(COLOR_RESET)
            .append(COLOR_GREEN)
            .append(appName)
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_NODE)
            .append(COLOR_WHITE)
            .append("Version: ")
            .append(COLOR_RESET)
            .append(COLOR_GREEN)
            .append(appVersion)
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_NODE)
            .append(COLOR_WHITE)
            .append("Port:    ")
            .append(COLOR_RESET)
            .append(COLOR_GREEN)
            .append(port)
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_LAST_NODE)
            .append(COLOR_WHITE)
            .append("Context: ")
            .append(COLOR_RESET)
            .append(COLOR_GREEN)
            .append(path.isEmpty() ? "/" : path)
            .append(COLOR_RESET)
            .append("\n\n");
    }
    
    /**
     * 添加访问URL信息
     */
    private static void appendAccessUrls(StringBuilder output, String port, String path, String ip) {
        output.append(COLOR_BOLD)
            .append(COLOR_BLUE)
            .append("Access URLs:")
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_NODE)
            .append(COLOR_WHITE)
            .append("Local:   ")
            .append(COLOR_RESET)
            .append(COLOR_CYAN)
            .append("http://localhost:")
            .append(port)
            .append(path)
            .append("/")
            .append(COLOR_RESET)
            .append("\n")
            .append(TREE_LAST_NODE)
            .append(COLOR_WHITE)
            .append("External:")
            .append(COLOR_RESET)
            .append(" ")
            .append(COLOR_CYAN)
            .append("http://")
            .append(ip)
            .append(":")
            .append(port)
            .append(path)
            .append("/")
            .append(COLOR_RESET)
            .append("\n\n");
    }
    
    /**
     * 添加暴露的API信息
     */
    private static void appendExposedApis(StringBuilder output) {
        output.append(COLOR_BOLD).append(COLOR_BLUE).append("Exposed APIs:").append(COLOR_RESET).append("\n");
        
        if (!GLOBAL_API_CACHE.isEmpty()) {
            GlobalApiOutEntity[] apis = GLOBAL_API_CACHE.toArray(new GlobalApiOutEntity[0]);
            for (int i = 0; i < apis.length; i++) {
                GlobalApiOutEntity api = apis[i];
                // 判断是否为最后一个元素，使用不同的前缀符号
                String prefix = (i == apis.length - 1) ? TREE_LAST_NODE : TREE_NODE;
                
                // 显示接口信息
                appendApiInfo(output, api, prefix, i == apis.length - 1);
            }
        }
        else {
            output.append(TREE_LAST_NODE)
                .append(COLOR_YELLOW)
                .append("No APIs exposed")
                .append(COLOR_RESET)
                .append("\n");
        }
        output.append("\n");
    }
    
    /**
     * 添加单个API信息
     */
    private static void appendApiInfo(StringBuilder output, GlobalApiOutEntity api, String prefix, boolean isLast) {
        output.append(prefix);
        
        // 显示接口描述信息（如果存在）
        if (hasComment(api)) {
            output.append(COLOR_YELLOW).append(api.getComment()).append(COLOR_RESET).append(" - ");
            
            // 显示请求方式和路径在同一行
            output.append(COLOR_WHITE)
                .append(api.getHttpMethod())
                .append(" ")
                .append(COLOR_RESET)
                .append(COLOR_CYAN)
                .append(api.getPath())
                .append(COLOR_RESET)
                .append("\n");
        }
        else {
            // 如果没有描述信息，直接显示请求方式和路径
            output.append(COLOR_WHITE)
                .append(api.getHttpMethod())
                .append(" ")
                .append(COLOR_RESET)
                .append(COLOR_CYAN)
                .append(api.getPath())
                .append(COLOR_RESET)
                .append("\n");
        }
        
        // 如果有参数信息，则在下一行显示
        if (hasParameters(api)) {
            String paramPrefix = isLast ? "   " : TREE_BAR;
            output.append(paramPrefix)
                .append("   ")
                .append(COLOR_WHITE)
                .append(".Parameters: ")
                .append(COLOR_RESET)
                .append(COLOR_GREEN)
                .append(api.getParams())
                .append(COLOR_RESET)
                .append("\n");
        }
    }
    
    /**
     * 检查API是否有描述信息
     */
    private static boolean hasComment(GlobalApiOutEntity api) {
        return api.getComment() != null && !api.getComment().trim().isEmpty();
    }
    
    /**
     * 检查API是否有参数信息
     */
    private static boolean hasParameters(GlobalApiOutEntity api) {
        return api.getParams() != null && !api.getParams().isEmpty() && !"无参数".equals(api.getParams());
    }
    
    /**
     * 添加启动时间信息
     */
    private static void appendStartupTime(StringBuilder output, Instant startTime) {
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        long millis = duration.toMillisPart();
        
        // 增加更多装饰性小火箭
        output.append(COLOR_GREEN)
            .append("🚀  Application started successfully! ")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_CYAN)
            .append("⏱️  Startup Time: ")
            .append(COLOR_RESET)
            .append(COLOR_YELLOW)
            .append(seconds)
            .append("s ")
            .append(millis)
            .append("ms")
            .append(COLOR_RESET)
            .append("\n")
            .append(COLOR_MAGENTA)
            .append("╰───────────────────────────────")
            .append(COLOR_RED)
            .append(" ⭐ ")
            .append(COLOR_ORANGE)
            .append("Application Running! ")
            .append(COLOR_RED)
            .append(" ⭐ ")
            .append(COLOR_MAGENTA)
            .append("───────────────────────────────╯")
            .append(COLOR_RESET)
            .append("\n");
    }
    
    // region 历史版本
    // package com.gqtian.common.config;
    //
    // import java.net.InetAddress;
    // import java.time.Duration;
    // import java.time.Instant;
    // import java.util.concurrent.LinkedBlockingQueue;
    //
    // import org.springframework.boot.SpringApplication;
    // import org.springframework.context.ConfigurableApplicationContext;
    // import org.springframework.core.env.Environment;
    //
    // import com.gqtian.common.model.GlobalApiOutEntity;
    //
    // import lombok.extern.slf4j.Slf4j;
    //
    /// **
    // * 👻 启动工具
    // *
    // * @author gqtian
    // * @className LaunchUtil
    // * @date 2024/4/8 13:53
    // */
    // @Slf4j
    // public class GlobalBeautifyStart {
    //
    // public static final String SERVER_PORT = "server.port";
    //
    // private static final String SERVER_SERVLET_CONTEXT_PATH_ = "server.servlet.context-path";
    //
    // private static final String SPRING_APPLICATION_NAME = "spring.application.name";
    //
    // private static final String SPRING_APPLICATION_VERSION = "spring.application.version";
    //
    // /**
    // * 开放接口请求
    // */
    // public final static LinkedBlockingQueue<GlobalApiOutEntity> GLOBAL_API_CACHE = new LinkedBlockingQueue<>();
    //
    // /**
    // * 启动
    // */
    // public static <T> void start(T inClass, String[] args) {
    // // 将startTime移到方法开始处，确保计算完整的启动时间
    // Instant startTime = Instant.now();
    // // 启动应用
    // ConfigurableApplicationContext application = SpringApplication.run((Class<?>)inClass, args);
    // // 输出启动信息
    // doOutLog(application, startTime);
    // }
    //
    // /**
    // * 输出启动信息
    // *
    // * @param application 上下文
    // * @param startTime 上下文启动时间
    // */
    // private static void doOutLog(ConfigurableApplicationContext application, Instant startTime) {
    // try {
    // Environment environment = application.getEnvironment();
    // String ip = InetAddress.getLocalHost().getHostAddress();
    // String port = environment.getProperty(SERVER_PORT);
    // String property = environment.getProperty(SERVER_SERVLET_CONTEXT_PATH_);
    // String path = property == null ? "" : property;
    // String appName = environment.getProperty(SPRING_APPLICATION_NAME, "未配置项目名");
    // String appVersion = environment.getProperty(SPRING_APPLICATION_VERSION, "1.0.0");
    //
    // // 构建输出信息
    // StringBuilder output = new StringBuilder();
    //
    // // 使用banner.txt中的图案，增加更多彩色装饰
    // output.append("\n");
    // output.append("\u001B[35m╭───────────────────────────────\u001B[91m ⭐ \u001B[33mApplication " + "Starting! "
    // + "\u001B[91m ⭐ \u001B[35m───────────────────────────────╮\u001B[0m\n");
    // output.append("""
    // \u001B[36m \u001B[0m
    // \u001B[36m ┏┓ ┏┓\u001B[91m+ +\u001B[0m
    // \u001B[36m ┏┛┻━━━┛┻┓\u001B[91m + +\u001B[0m
    // \u001B[36m ┃ ┃\u001B[93m \u001B[0m
    // \u001B[36m ┃ ━ ┃\u001B[91m ++ + + +\u001B[0m
    // \u001B[36m \u001B[91m ████━████\u001B[36m ┃\u001B[91m+\u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m +\u001B[0m
    // \u001B[36m ┃ ┻ ┃\u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m + +\u001B[0m
    // \u001B[36m ┗━┓ ┏━┛\u001B[0m
    // \u001B[36m ┃ ┃\u001B[93m \u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m + + + +\u001B[0m
    // \u001B[36m ┃ ┃\u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m + \u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m \u001B[0m
    // \u001B[36m ┃ ┃\u001B[91m + \u001B[0m
    // \u001B[36m ┃ ┗━━━┓\u001B[91m + +\u001B[0m
    // \u001B[36m ┃\u001B[95m ┣┓\u001B[36m \u001B[0m
    // \u001B[36m ┃\u001B[95m ┏┛\u001B[36m x x x x x x x x x x x x x x x x x x\u001B[0m
    // \u001B[36m ┗┓┓┏━┳┓┏┛\u001B[91m + + + +\u001B[0m
    // \u001B[36m ┃┫┫ ┃┫┫\u001B[0m
    // \u001B[36m ┗┻┛ ┗┻┛\u001B[91m+ + + +\u001B[0m
    // \u001B[36m \u001B[0m
    // """);
    //
    // output.append("\u001B[92m──────────────────────────────────────────────────────────────\u001B[0m\n");
    // output.append("\n");
    // // 配置信息
    // String[] activeProfiles = environment.getActiveProfiles();
    // if (activeProfiles.length > 0) {
    // output.append("\u001B[1m\u001B[94mActive Profiles:\u001B[0m\n");
    // output.append("\u001B[95m└─ \u001B[0m");
    // for (int i = 0; i < activeProfiles.length; i++) {
    // output.append("\u001B[93m").append(activeProfiles[i]).append("\u001B[0m");
    // if (i < activeProfiles.length - 1) {
    // output.append("\u001B[97m, \u001B[0m");
    // }
    // }
    // output.append("\n");
    // }
    //
    // // 应用基本信息
    // output.append("\n\u001B[1m\u001B[94mApplication Information:\u001B[0m\n");
    // output.append("\u001B[95m├─ \u001B[0m\u001B[97mName: \u001B[0m")
    // .append("\u001B[92m")
    // .append(appName)
    // .append("\u001B[0m\n");
    // output.append("\u001B[95m├─ \u001B[0m\u001B[97mVersion: \u001B[0m")
    // .append("\u001B[92m")
    // .append(appVersion)
    // .append("\u001B[0m\n");
    // output.append("\u001B[95m├─ \u001B[0m\u001B[97mPort: \u001B[0m")
    // .append("\u001B[92m")
    // .append(port)
    // .append("\u001B[0m\n");
    // output.append("\u001B[95m└─ \u001B[0m\u001B[97mContext: \u001B[0m")
    // .append("\u001B[92m")
    // .append(path.isEmpty() ? "/" : path)
    // .append("\u001B[0m\n");
    //
    // output.append("\n");
    //
    // // 访问地址信息
    // output.append("\u001B[1m\u001B[94mAccess URLs:\u001B[0m\n");
    // output.append("\u001B[95m├─ \u001B[0m\u001B[97mLocal: \u001B[0m")
    // .append("\u001B[96mhttp://localhost:")
    // .append(port)
    // .append(path)
    // .append("/\u001B[0m\n");
    // output.append("\u001B[95m└─ \u001B[0m\u001B[97mExternal:\u001B[0m ")
    // .append("\u001B[96mhttp://")
    // .append(ip)
    // .append(":")
    // .append(port)
    // .append(path)
    // .append("/\u001B[0m\n");
    // output.append("\n");
    //
    // // 输出开放接口属性
    // if (!GLOBAL_API_CACHE.isEmpty()) {
    // output.append("\u001B[1m\u001B[94mExposed APIs:\u001B[0m\n");
    // GlobalApiOutEntity[] apis = GLOBAL_API_CACHE.toArray(new GlobalApiOutEntity[0]);
    // for (int i = 0; i < apis.length; i++) {
    // GlobalApiOutEntity api = apis[i];
    // // 判断是否为最后一个元素，使用不同的前缀符号
    // String prefix = (i == apis.length - 1) ? "\u001B[95m└─ \u001B[0m" : "\u001B[95m├─ \u001B[0m";
    //
    // // 显示接口描述信息（如果存在）
    // if (api.getComment() != null && !api.getComment().trim().isEmpty()) {
    // output.append(prefix)
    // .append("\u001B[93m")
    // .append(api.getComment())
    // .append("\u001B[0m")
    // .append(" - ");
    //
    // // 显示请求方式和路径在同一行
    // output.append("\u001B[97m")
    // .append(api.getHttpMethod())
    // .append(" \u001B[0m")
    // .append("\u001B[96m")
    // .append(api.getPath())
    // .append("\u001B[0m\n");
    // }
    // else {
    // // 如果没有描述信息，直接显示请求方式和路径
    // output.append(prefix)
    // .append("\u001B[97m")
    // .append(api.getHttpMethod())
    // .append(" \u001B[0m")
    // .append("\u001B[96m")
    // .append(api.getPath())
    // .append("\u001B[0m\n");
    // }
    //
    // // 如果有参数信息，则在下一行显示
    // if (api.getParams() != null && !api.getParams().isEmpty() && !"无参数".equals(api.getParams())) {
    // String paramPrefix = (i == apis.length - 1) ? " " : "\u001B[95m│ \u001B[0m";
    // output.append(paramPrefix)
    // .append(" \u001B[97m.Parameters: \u001B[0m")
    // .append("\u001B[92m")
    // .append(api.getParams())
    // .append("\u001B[0m\n");
    // }
    // }
    // output.append("\n");
    // }
    // else {
    // output.append("\u001B[1m\u001B[94mExposed APIs:\u001B[0m\n");
    // output.append("\u001B[95m└─ \u001B[0m\u001B[93mNo APIs exposed\u001B[0m\n\n");
    // }
    //
    // // 启动耗时统计
    // Duration duration = Duration.between(startTime, Instant.now());
    // long seconds = duration.getSeconds();
    // long millis = duration.toMillisPart();
    //
    // // 增加更多装饰性小火箭
    // output.append("\u001B[92m🚀 Application started successfully! \u001B[0m\n");
    // output.append("\u001B[96m⏱️ Startup Time: \u001B[0m")
    // .append("\u001B[93m")
    // .append(seconds)
    // .append("s ")
    // .append(millis)
    // .append("ms\u001B[0m\n");
    // output.append("\u001B[35m╰───────────────────────────────\u001B[91m ⭐ " + "\u001B[33mApplication "
    // + "Running! " + "\u001B[91m ⭐ \u001B[35m───────────────────────────────╯\u001B[0m\n");
    //
    // // 一次性输出所有信息
    // log.info("{}", output);
    //
    // }
    // catch (Exception e) {
    // log.error("启动失败", e);
    // }
    // }
    //
    // }
    // endregion
}