package com.gqtian.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Flex 配置类 配置SQL审计、逻辑删除等功能
 *
 * @author 【全栈学习关注公众号：程序员Mars】
 * @version 1.0
 * @date 2025-06-02 14:39:44
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.gqtian.common.mapper") // 读取commonModel包下的Mapper
public class MyBatisConfiguration {
    
    // public MyBatisConfiguration()
    // {
    //
    // // 开启审计功能
    // AuditManager.setAuditEnable(true);
    // log.info("🔍 MyBatis-Flex 审计功能已开启");
    //
    // // 设置 SQL 审计收集器 - 美化日志输出
    // AuditManager.setMessageCollector(auditMessage -> {
    // long elapsedTime = auditMessage.getElapsedTime();
    // String sqlType = getSqlType(auditMessage.getFullSql());
    // String tableName = extractTableName(auditMessage.getFullSql());
    // String sql = formatSql(auditMessage.getFullSql());
    // String coloredSql = colorizeSQL(auditMessage.getFullSql(), sqlType);
    //
    // // 构建美化的日志输出
    // StringBuilder logBuilder = new StringBuilder();
    //
    // // 慢SQL阈值判断和图标选择
    // String icon, level, headerColor;
    // if (elapsedTime > 1000) {
    // icon = "🐌";
    // level = "慢SQL警告";
    // headerColor = "\u001B[91m"; // 亮红色
    // }
    // else if (elapsedTime > 500) {
    // icon = "⚠️";
    // level = "性能提醒";
    // headerColor = "\u001B[93m"; // 亮黄色
    // }
    // else {
    // icon = "✅";
    // level = "正常执行";
    // headerColor = "\u001B[92m"; // 亮绿色
    // }
    //
    // logBuilder.append("\n");
    // logBuilder.append(String.format("%s %s %-6s │ 类型: %-8s │ 耗时: %4dms │ 表: %-15s\u001B[0m\n",
    // headerColor,
    // icon,
    // level,
    // sqlType,
    // elapsedTime,
    // tableName));
    // logBuilder.append("SQL: ").append(coloredSql).append("\n");
    //
    // // // 记录到SQL日志控制器
    // // SqlLogController.addSqlLog(auditMessage.getFullSql(), sqlType, elapsedTime, tableName);
    // // 根据性能选择日志级别
    // if (elapsedTime > 1000) {
    // log.warn(logBuilder.toString());
    // }
    // else if (elapsedTime > 500) {
    // log.warn(logBuilder.toString());
    // }
    // else {
    // log.info(logBuilder.toString());
    // }
    // });
    //
    // log.info("✅ MyBatis-Flex SQL审计收集器配置完成");
    // }
    //
    // /**
    // * 获取SQL类型
    // */
    // private String getSqlType(String sql) {
    // if (sql == null || sql.trim().isEmpty()) {
    // return "UNKNOWN";
    // }
    //
    // String upperSql = sql.trim().toUpperCase();
    // if (upperSql.startsWith("SELECT")) {
    // return "SELECT";
    // }
    // else if (upperSql.startsWith("INSERT")) {
    // return "INSERT";
    // }
    // else if (upperSql.startsWith("UPDATE")) {
    // return "UPDATE";
    // }
    // else if (upperSql.startsWith("DELETE")) {
    // return "DELETE";
    // }
    // else if (upperSql.startsWith("CREATE")) {
    // return "CREATE";
    // }
    // else if (upperSql.startsWith("DROP")) {
    // return "DROP";
    // }
    // else if (upperSql.startsWith("ALTER")) {
    // return "ALTER";
    // }
    // else {
    // return "OTHER";
    // }
    // }
    //
    // /**
    // * 从SQL中提取表名（简单实现）
    // */
    // private String extractTableName(String sql) {
    // if (sql == null || sql.trim().isEmpty()) {
    // return "unknown";
    // }
    //
    // try {
    // String upperSql = sql.trim().toUpperCase();
    // String[] words = upperSql.split("\\s+");
    //
    // for (int i = 0; i < words.length - 1; i++) {
    // if ("FROM".equals(words[i]) || "INTO".equals(words[i]) || "UPDATE".equals(words[i])) {
    // return words[i + 1].replaceAll("[^a-zA-Z0-9_]", "");
    // }
    // }
    // }
    // catch (Exception e) {
    // log.debug("提取表名失败: {}", e.getMessage());
    // }
    //
    // return "unknown";
    // }
    //
    // /**
    // * 格式化SQL语句，让其更易读
    // */
    // private String formatSql(String sql) {
    // if (sql == null || sql.trim().isEmpty()) {
    // return "N/A";
    // }
    //
    // // 移除多余的空格和换行
    // return sql.replaceAll("\\s+", " ").trim();
    // }
    //
    // /**
    // * 为SQL语句添加颜色
    // */
    // private String colorizeSQL(String sql, String sqlType) {
    // if (sql == null || sql.trim().isEmpty()) {
    // return "N/A";
    // }
    //
    // // 清理SQL
    // String cleanSql = sql.replaceAll("\\s+", " ").trim();
    //
    // // 根据SQL类型选择颜色
    // String typeColor = switch (sqlType) {
    // case "SELECT" -> "\u001B[94m"; // 亮蓝色
    // case "INSERT" -> "\u001B[92m"; // 亮绿色
    // case "UPDATE" -> "\u001B[93m"; // 亮黄色
    // case "DELETE" -> "\u001B[91m"; // 亮红色
    // default -> "\u001B[95m"; // 亮紫色
    // };
    //
    // // 高亮SQL关键字
    // String coloredSql = cleanSql
    // .replaceAll(
    // "(?i)\\b(SELECT|FROM|WHERE|JOIN|LEFT|RIGHT|INNER|OUTER|ON|GROUP BY|ORDER " + "BY|HAVING|LIMIT)\\b",
    // "\u001B[96m$1\u001B[0m" + typeColor) // 青色关键字
    // .replaceAll("(?i)\\b(INSERT|INTO|VALUES)\\b", "\u001B[96m$1\u001B[0m" + typeColor) // 青色关键字
    // .replaceAll("(?i)\\b(UPDATE|SET)\\b", "\u001B[96m$1\u001B[0m" + typeColor) // 青色关键字
    // .replaceAll("(?i)\\b(DELETE)\\b", "\u001B[96m$1\u001B[0m" + typeColor) // 青色关键字
    // .replaceAll("(?i)\\b(AND|OR|NOT|IN|EXISTS|BETWEEN|LIKE|IS|NULL)\\b", "\u001B[97m$1\u001B[0m" + typeColor) //
    // 白色逻辑操作符
    // .replaceAll("(?i)\\b(COUNT|SUM|AVG|MAX|MIN|DISTINCT)\\b", "\u001B[35m$1\u001B[0m" + typeColor) // 紫色函数
    // .replaceAll("'([^']*)'", "\u001B[32m'$1'\u001B[0m" + typeColor) // 绿色字符串
    // .replaceAll("\\b(\\d+)\\b", "\u001B[33m$1\u001B[0m" + typeColor); // 黄色数字
    //
    // return typeColor + coloredSql + "\u001B[0m";
    // }
    //
    // /**
    // * 格式化SQL用于复制 - 去除颜色代码，保持原始格式
    // */
    // private String formatSqlForCopy(String sql) {
    // if (sql == null || sql.trim().isEmpty()) {
    // return "N/A";
    // }
    //
    // // 美化SQL格式，便于复制使用
    // String formatted = sql.trim()
    // .replaceAll("(?i)\\bSELECT\\b", "\nSELECT")
    // .replaceAll("(?i)\\bFROM\\b", "\nFROM")
    // .replaceAll("(?i)\\bWHERE\\b", "\nWHERE")
    // .replaceAll("(?i)\\bAND\\b", "\n AND")
    // .replaceAll("(?i)\\bOR\\b", "\n OR")
    // .replaceAll("(?i)\\bJOIN\\b", "\nJOIN")
    // .replaceAll("(?i)\\bLEFT JOIN\\b", "\nLEFT JOIN")
    // .replaceAll("(?i)\\bRIGHT JOIN\\b", "\nRIGHT JOIN")
    // .replaceAll("(?i)\\bINNER JOIN\\b", "\nINNER JOIN")
    // .replaceAll("(?i)\\bORDER BY\\b", "\nORDER BY")
    // .replaceAll("(?i)\\bGROUP BY\\b", "\nGROUP BY")
    // .replaceAll("(?i)\\bHAVING\\b", "\nHAVING")
    // .replaceAll("(?i)\\bLIMIT\\b", "\nLIMIT")
    // .replaceAll("(?i)\\bINSERT INTO\\b", "\nINSERT INTO")
    // .replaceAll("(?i)\\bVALUES\\b", "\nVALUES")
    // .replaceAll("(?i)\\bUPDATE\\b", "\nUPDATE")
    // .replaceAll("(?i)\\bSET\\b", "\nSET")
    // .replaceAll("\\n\\s*\\n", "\n") // 移除多余空行
    // .replaceAll("\\s+", " ") // 统一空格
    // .trim();
    //
    // // 如果格式化后的SQL太长，提供简化版本
    // if (formatted.length() > 200) {
    // return sql.replaceAll("\\s+", " ").trim();
    // }
    //
    // return formatted;
    // }
    
}
