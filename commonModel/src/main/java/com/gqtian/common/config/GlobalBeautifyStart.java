package com.gqtian.common.config;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.gqtian.common.model.GlobalApiOutEntity;
import com.gqtian.common.model.GlobalApiOutEntity.ParamEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 美化启动工具类
 * <p>
 * 提供美化的应用启动信息显示，包括：
 * <ul>
 *   <li>应用基本信息（名称、版本、端口等）</li>
 *   <li>访问地址（本地和外部地址）</li>
 *   <li>活跃配置文件</li>
 *   <li>所有对外开放API接口列表</li>
 *   <li>启动耗时统计</li>
 * </ul>
 * </p>
 * <p>
 * 使用彩色终端输出，提供更好的视觉效果
 * </p>
 *
 * @author gqtian
 * @date 2024/4/8 13:53
 */
@Slf4j
public class GlobalBeautifyStart {

    /**
     * 全局API信息缓存队列
     * <p>用于存储所有被@GlobalApiOut注解标记的接口信息</p>
     */
    public final static LinkedBlockingQueue<GlobalApiOutEntity> GLOBAL_API_CACHE = new LinkedBlockingQueue<>();

    /** 服务端口配置键 */
    private static final String SERVER_PORT = "server.port";

    /** Servlet上下文路径配置键 */
    private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";

    /** 应用名称配置键 */
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    /** 应用版本配置键 */
    private static final String SPRING_APPLICATION_VERSION = "spring.application.version";

    // ===== 以下为终端彩色输出相关常量 =====
    /** ANSI颜色码前缀 */
    private static final String COLOR_PREFIX = "\u001B[";

    /** 重置颜色 */
    private static final String COLOR_RESET = COLOR_PREFIX + "0m";

    /** 品红色 */
    private static final String COLOR_MAGENTA = COLOR_PREFIX + "35m";

    /** 青色 */
    private static final String COLOR_CYAN = COLOR_PREFIX + "36m";

    /** 亮蓝色 */
    private static final String COLOR_BLUE = COLOR_PREFIX + "94m";

    /** 亮绿色 */
    private static final String COLOR_GREEN = COLOR_PREFIX + "92m";

    /** 亮黄色 */
    private static final String COLOR_YELLOW = COLOR_PREFIX + "93m";

    /** 亮白色 */
    private static final String COLOR_WHITE = COLOR_PREFIX + "97m";

    /** 粉红色 */
    private static final String COLOR_PINK = COLOR_PREFIX + "95m";

    /** 亮红色 */
    private static final String COLOR_RED = COLOR_PREFIX + "91m";

    /** 橙色 */
    private static final String COLOR_ORANGE = COLOR_PREFIX + "33m";

    /** 粗体 */
    private static final String COLOR_BOLD = COLOR_PREFIX + "1m";

    // ===== 树形结构符号 =====
    /** 最后一个节点 */
    private static final String TREE_LAST_NODE = COLOR_PINK + "└─ " + COLOR_RESET;

    /** 中间节点 */
    private static final String TREE_NODE = COLOR_PINK + "├─ " + COLOR_RESET;

    /** 树形竖线 */
    private static final String TREE_BAR = COLOR_PINK + "│  " + COLOR_RESET;

    /**
     * 启动Spring Boot应用并输出美化的启动信息
     *
     * @param inClass Spring Boot启动类
     * @param args 启动参数
     * @param <T> 启动类类型
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
     * <p>
     * 构建并输出包含应用配置、访问地址、API列表等信息的美化启动日志
     * </p>
     *
     * @param application Spring应用上下文
     * @param startTime 启动开始时间
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

        } catch (Exception e) {
            log.error("启动失败", e);
        }
    }

    /**
     * 添加启动横幅
     * <p>在控制台输出一个ASCII艺术横幅</p>
     *
     * @param output 字符串构建器
     */
    private static void appendBanner(StringBuilder output) {
        output.append("\n").append(COLOR_MAGENTA).append("╭───────────────────────────────").append(COLOR_RED)
            .append(" ⭐ ").append(COLOR_ORANGE).append("Application Starting! ").append(COLOR_RED).append(" ⭐ ")
            .append(COLOR_MAGENTA).append("───────────────────────────────╮").append(COLOR_RESET).append("\n");

        output.append(COLOR_CYAN).append(" \u001B[0m\n").append(COLOR_CYAN).append(" 　  ┏┓　 ┏┓").append(COLOR_RED)
            .append("+ +").append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　┏┛┻━━━┛┻┓").append(COLOR_RED)
            .append(" + +").append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　┃　　　　　　 ┃")
            .append(COLOR_YELLOW).append(" 　").append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　┃　　　━　　　┃")
            .append(COLOR_RED).append(" ++ + + +").append(COLOR_RESET).append("\n").append(COLOR_RED)
            .append(" ████━████").append(COLOR_CYAN).append(" ┃").append(COLOR_RED).append("+").append(COLOR_RESET)
            .append("\n").append(COLOR_CYAN).append(" 　┃　　　　　　 ┃").append(COLOR_RED).append(" +").append(COLOR_RESET)
            .append("\n").append(COLOR_CYAN).append(" 　┃　　　┻　　　┃").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　┃　　　　　　 ┃").append(COLOR_RED).append(" + +").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　┗━┓　　　┏━┛").append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　　　┃　　　┃")
            .append(COLOR_YELLOW).append("　　　　　　　　　　　").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　　　┃　　　┃").append(COLOR_RED).append(" + + + +").append(COLOR_RESET).append("\n")
            .append(COLOR_CYAN).append(" 　　　┃　　　┃").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　　　┃　　　┃").append(COLOR_RED).append(" +  ").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　　　┃　　　┃").append(COLOR_RED).append("    ").append(COLOR_RESET).append("\n").append(COLOR_CYAN)
            .append(" 　　　┃　　　┃").append(COLOR_RED).append("　　+　　　　　　　　　").append(COLOR_RESET).append("\n")
            .append(COLOR_CYAN).append(" 　　　┃　 　　┗━━━┓").append(COLOR_RED).append(" + +").append(COLOR_RESET)
            .append("\n").append(COLOR_CYAN).append(" 　　　┃").append(COLOR_PINK).append(" 　　　　　　　┣┓").append(COLOR_CYAN)
            .append("  ").append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　　　┃").append(COLOR_PINK)
            .append(" 　　　　　　　┏┛").append(COLOR_CYAN).append(" x x x x x x x x x x x x x x x x x x").append(COLOR_RESET)
            .append("\n").append(COLOR_CYAN).append(" 　　　┗┓┓┏━┳┓┏┛").append(COLOR_RED).append(" + + + +")
            .append(COLOR_RESET).append("\n").append(COLOR_CYAN).append(" 　　　　┃┫┫　┃┫┫").append(COLOR_RESET).append("\n")
            .append(COLOR_CYAN).append(" 　　　　┗┻┛　┗┻┛").append(COLOR_RED).append("+ + + +").append(COLOR_RESET)
            .append("\n").append(COLOR_CYAN).append(" \u001B[0m\n");

        output.append(COLOR_GREEN).append("──────────────────────────────────────────────────────────────")
            .append(COLOR_RESET).append("\n\n");
    }

    /**
     * 添加活跃配置信息
     * <p>显示当前应用激活的配置文件（profiles）</p>
     *
     * @param output 字符串构建器
     * @param environment Spring环境对象
     */
    private static void appendActiveProfiles(StringBuilder output, Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            output.append(COLOR_BOLD).append(COLOR_BLUE).append("Active Profiles:").append(COLOR_RESET).append("\n")
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
     * <p>显示应用名称、版本、端口、上下文路径等信息</p>
     *
     * @param output 字符串构建器
     * @param appName 应用名称
     * @param appVersion 应用版本
     * @param port 端口号
     * @param path 上下文路径
     */
    private static void appendApplicationInfo(StringBuilder output, String appName, String appVersion, String port,
        String path) {
        output.append("\n").append(COLOR_BOLD).append(COLOR_BLUE).append("Application Information:").append(COLOR_RESET)
            .append("\n").append(TREE_NODE).append(COLOR_WHITE).append("Name:    ").append(COLOR_RESET)
            .append(COLOR_GREEN).append(appName).append(COLOR_RESET).append("\n").append(TREE_NODE).append(COLOR_WHITE)
            .append("Version: ").append(COLOR_RESET).append(COLOR_GREEN).append(appVersion).append(COLOR_RESET)
            .append("\n").append(TREE_NODE).append(COLOR_WHITE).append("Port:    ").append(COLOR_RESET)
            .append(COLOR_GREEN).append(port).append(COLOR_RESET).append("\n").append(TREE_LAST_NODE)
            .append(COLOR_WHITE).append("Context: ").append(COLOR_RESET).append(COLOR_GREEN)
            .append(path.isEmpty() ? "/" : path).append(COLOR_RESET).append("\n\n");
    }

    /**
     * 添加访问URL信息
     * <p>显示应用的本地和外部访问地址</p>
     *
     * @param output 字符串构建器
     * @param port 端口号
     * @param path 上下文路径
     * @param ip 服务器IP地址
     */
    private static void appendAccessUrls(StringBuilder output, String port, String path, String ip) {
        String basePath = "http://localhost:" + port + path;
        output.append(COLOR_BOLD).append(COLOR_BLUE).append("Access URLs:").append(COLOR_RESET).append("\n")
            .append(TREE_NODE).append(COLOR_WHITE).append("Home:    ").append(COLOR_RESET).append(" ")
            .append(COLOR_CYAN).append("http://").append(ip).append(":").append(port).append(path)
            .append("/CommonIndex.html").append(COLOR_RESET).append("\n").append(TREE_NODE).append(COLOR_WHITE)
            .append("Local:    ").append(COLOR_RESET).append(COLOR_CYAN).append(basePath).append("/")
            .append(COLOR_RESET).append("\n").append(TREE_LAST_NODE).append(COLOR_WHITE).append("External:")
            .append(COLOR_RESET).append(" ").append(COLOR_CYAN).append("http://").append(ip).append(":").append(port)
            .append(path).append("/").append(COLOR_RESET).append("\n\n");
    }

    /**
     * 添加对外暴露的API信息
     * <p>显示所有被@GlobalApiOut注解标记的API接口列表</p>
     *
     * @param output 字符串构建器
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
        } else {
            output.append(TREE_LAST_NODE).append(COLOR_YELLOW).append("No APIs Exposed").append(COLOR_RESET)
                .append("\n");
        }
        output.append("\n");
    }

    /**
     * 添加单个API信息
     * <p>格式化输出单个API的详细信息，包括请求方法、路径和参数</p>
     *
     * @param output 字符串构建器
     * @param api API信息实体
     * @param prefix 树形结构前缀符号
     * @param isLast 是否为最后一个API
     */
    private static void appendApiInfo(StringBuilder output, GlobalApiOutEntity api, String prefix, boolean isLast) {
        output.append(prefix);

        // 显示接口描述信息（如果存在）
        if (hasComment(api)) {
            output.append(COLOR_YELLOW).append(api.getComment()).append(COLOR_RESET).append(" - ");

            // 显示请求方式和路径在同一行
            output.append(COLOR_WHITE).append(api.getHttpMethod()).append(" ").append(COLOR_RESET).append(COLOR_CYAN)
                .append(api.getPath()).append(COLOR_RESET).append("\n");
        } else {
            // 如果没有描述信息，直接显示请求方式和路径
            output.append(COLOR_WHITE).append(api.getHttpMethod()).append(" ").append(COLOR_RESET).append(COLOR_CYAN)
                .append(api.getPath()).append(COLOR_RESET).append("\n");
        }

        // 如果有参数信息，则在下一行显示
        if (hasParameters(api)) {
            String paramPrefix = isLast ? "   " : TREE_BAR;

            StringBuilder paramOutput = new StringBuilder();
            for (ParamEntity param : api.getParams()) {
                if (!paramOutput.isEmpty()) {
                    paramOutput.append(COLOR_WHITE).append(" | ");
                }
                paramOutput.append(COLOR_RESET).append(COLOR_GREEN).append(param.getType()).append(": ")
                    .append(COLOR_RESET).append(COLOR_CYAN).append(param.getName()).append(COLOR_RESET);
            }

            output.append(paramPrefix).append("   ").append(COLOR_WHITE).append(".Parameters: ").append(COLOR_RESET)
                .append(COLOR_GREEN).append(paramOutput).append(COLOR_RESET).append("\n");
        }
    }

    /**
     * 检查API是否有描述信息
     *
     * @param api API信息实体
     * @return 是否有描述
     */
    private static boolean hasComment(GlobalApiOutEntity api) {
        return api.getComment() != null && !api.getComment().trim().isEmpty();
    }

    /**
     * 检查API是否有参数信息
     *
     * @param api API信息实体
     * @return 是否有参数
     */
    private static boolean hasParameters(GlobalApiOutEntity api) {
        return api.getParams() != null && !api.getParams().isEmpty();
    }

    /**
     * 添加启动时间信息
     * <p>计算并显示应用的启动耗时</p>
     *
     * @param output 字符串构建器
     * @param startTime 启动开始时间
     */
    private static void appendStartupTime(StringBuilder output, Instant startTime) {
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        long millis = duration.toMillisPart();

        // 增加更多装饰性小火箭
        output.append(COLOR_GREEN).append("🚀  Application started successfully! ").append(COLOR_RESET).append("\n")
            .append(COLOR_CYAN).append("⏱️  Startup Time: ").append(COLOR_RESET).append(COLOR_YELLOW).append(seconds)
            .append("s ").append(millis).append("ms").append(COLOR_RESET).append("\n").append(COLOR_MAGENTA)
            .append("╰───────────────────────────────").append(COLOR_RED).append(" ⭐ ").append(COLOR_ORANGE)
            .append("Application Running! ").append(COLOR_RED).append(" ⭐ ").append(COLOR_MAGENTA)
            .append("───────────────────────────────╯").append(COLOR_RESET).append("\n");
    }

}