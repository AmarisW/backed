package com.gqtian.common.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis 完整SQL打印拦截器
 * <p>
 * 支持配置项：
 * <ul>
 *   <li>slowSqlThreshold - 慢SQL阈值（毫秒），默认1000ms</li>
 *   <li>enableSqlLog - 是否启用SQL日志，默认true</li>
 * </ul>
 * </p>
 *
 * @author gqtian
 * @date 2025/8/15
 */
@Slf4j
@Setter
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public class GlobalSqlPrintInterceptor implements Interceptor {

    /**
     * 慢SQL阈值（毫秒），默认1000ms
     */
    private long slowSqlThreshold = 1000L;

    /**
     * 是否启用SQL日志
     */
    private boolean enableSqlLog = true;

    /*
     * ┌─────────────────────────────────────────────────────────────────────────┐
     * │                            核心拦截方法                                  │
     * └─────────────────────────────────────────────────────────────────────────┘
     */

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enableSqlLog) {
            return invocation.proceed();
        }

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
        boolean isSlowSql = costTime > slowSqlThreshold;

        String logContent = String.format("""

            ══════════════════════════════  SQL执行日志  ══════════════════════════════
            ║ SqlId  : %s
            ║ Status : %s
            ║ Cost   : %d ms%s
            ║ SQL    : %s

            ═══════════════════════════════════════════════════════════════════════════
            """, sqlId, isSlowSql ? "[慢]" : "[正常]", costTime,
            isSlowSql ? " (阈值:" + slowSqlThreshold + "ms)" : "", sql);

        if (isSlowSql) {
            log.warn(logContent);
        } else {
            log.info(logContent);
        }
    }
}
