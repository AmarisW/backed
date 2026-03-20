package com.gqtian.common.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis 完整SQL打印拦截器  
 * @author gqtian
 * @date 2025/8/15
 */
@Slf4j
@RequiredArgsConstructor
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public class GlobalSqlPrintInterceptor implements Interceptor {

    /*
     * ┌─────────────────────────────────────────────────────────────────────────┐
     * │                            核心拦截方法                                  │
     * └─────────────────────────────────────────────────────────────────────────┘
     */

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // ═══════════════════════ 1. 提取SQL相关信息 ═══════════════════════
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        // ═══════════════════════ 2. 执行SQL并计时 ═══════════════════════
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = invocation.proceed();
        } finally {
            long costTime = System.currentTimeMillis() - startTime;

            // ═══════════════════════ 3. 构建完整SQL ═══════════════════════
            String sql = generateCompleteSql(configuration, boundSql);

            // ═══════════════════════ 4. 格式化输出 ═══════════════════════
            printSqlLog(sqlId, sql, costTime);
        }

        return result;
    }

    /*
     * ┌─────────────────────────────────────────────────────────────────────────┐
     * │                          SQL构建核心方法                                 │
     * └─────────────────────────────────────────────────────────────────────────┘
     */

    /**
     * 生成完整的SQL语句（参数已替换）
     *
     * @param configuration MyBatis配置对象
     * @param boundSql      绑定的SQL对象
     * @return 完整SQL字符串
     */
    private String generateCompleteSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // 格式化SQL（去除多余空白）
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        // ─────────────── 参数替换处理 ───────────────
        if (parameterMappings != null && !parameterMappings.isEmpty() && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

            // 情况1：参数对象有对应的类型处理器（基本类型/包装类型/日期等）
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql =
                    sql.replaceFirst("\\?", Matcher.quoteReplacement(formatParameterValue(parameterObject)));
            } else {
                // 情况2：复杂对象类型，通过反射获取属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    Object value = extractParameterValue(boundSql, metaObject, propertyName);
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(formatParameterValue(value)));
                }
            }
        }
        return sql;
    }

    /**
     * 提取参数值
     * <p>
     * 优先级：additionalParameter > 对象属性 > null
     */
    private Object extractParameterValue(BoundSql boundSql, MetaObject metaObject, String propertyName) {
        if (boundSql.hasAdditionalParameter(propertyName)) {
            return boundSql.getAdditionalParameter(propertyName);
        }
        if (metaObject.hasGetter(propertyName)) {
            return metaObject.getValue(propertyName);
        }
        return null;
    }

    /*
     * ┌─────────────────────────────────────────────────────────────────────────┐
     * │                          参数格式化方法                                  │
     * └─────────────────────────────────────────────────────────────────────────┘
     */

    /**
     * 格式化参数值为SQL字符串
     * <p>
     * 处理规则： ├─ String类型   → 添加单引号包裹 ├─ Date类型     → 格式化为日期字符串并添加单引号 ├─ null值       → 转为字符串"null" └─
     * 其他类型     → 直接toString()
     */
    private String formatParameterValue(Object value) {
        switch (value) {
            case null -> {
                return "null";
            }
            case String ignored -> {
                return "'" + value.toString().replace("'", "''") + "'";
            }
            case Date date -> {
                DateFormat dateFormat =
                    DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
                return "'" + dateFormat.format(date) + "'";
            }
            default -> {
            }
        }
        return value.toString();
    }

    /*
     * ┌─────────────────────────────────────────────────────────────────────────┐
     * │                          日志输出方法                                    │
     * └─────────────────────────────────────────────────────────────────────────┘
     */

    /**
     * 格式化打印SQL日志
     */
    private void printSqlLog(String sqlId, String sql, long costTime) {
        boolean isSlowSql = costTime > 1000;

        // ─────────────── 构建日志内容 ───────────────
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n╔══════════════════════════════════════════════════════════════════════════");
        logBuilder.append("\n║ 【SQL执行日志】").append(isSlowSql ? " ⚠️ 慢SQL警告" : "");
        logBuilder.append("\n╠══════════════════════════════════════════════════════════════════════════");
        logBuilder.append("\n║ SqlId    : ").append(sqlId);
        logBuilder.append("\n║ CostTime : ").append(costTime).append(" ms");
        if (isSlowSql) {
            logBuilder.append(" (超过阈值: ").append(1000).append("ms)");
        }
        logBuilder.append("\n╠──────────────────────────────────────────────────────────────────────────");
        logBuilder.append("\n║ SQL >>");
        logBuilder.append("\n║ ").append(sql);
        logBuilder.append("\n╚══════════════════════════════════════════════════════════════════════════");

        // ─────────────── 根据耗时选择日志级别 ───────────────
        if (isSlowSql) {
            log.warn(logBuilder.toString());
        } else {
            log.info(logBuilder.toString());
        }
    }

    @Override
    public void setProperties(Properties properties) {
        // 可通过配置文件传入自定义属性
    }
}
