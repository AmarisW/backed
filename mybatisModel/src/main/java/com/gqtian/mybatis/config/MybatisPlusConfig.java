package com.gqtian.mybatis.config;

import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MyBatis-Plus配置类
 * <p>
 * 功能说明：
 * 1. 配置MyBatis-Plus拦截器，包括分页插件和动态表名插件
 * 2. 支持动态表名替换功能，可在运行时替换SQL中的表名
 * 3. 自动扫描指定包路径下的Mapper接口
 *
 * @author gqtian
 * @date 2025/8/15 00:08
 */
@Slf4j
@Configuration
@MapperScan("com.gqtian.**.mapper")
@RequiredArgsConstructor
public class MybatisPlusConfig {

    /**
     * 表名替换配置属性
     * 用于动态表名替换功能的配置信息
     */
    private final TableReplaceProperties tableReplaceProperties;

    /**
     * 配置MyBatis-Plus拦截器
     * <p>
     * 拦截器执行顺序：分页插件 -> 动态表名插件
     * 注意：插件的添加顺序会影响执行顺序
     *
     * @return MyBatis-Plus拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 添加分页插件 - 提供物理分页支持
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        // 2. 添加动态表名插件 - 支持运行时动态替换表名
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor =
            dynamicTableNameInnerInterceptor();
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        return interceptor;
    }

    /**
     * 创建动态表名拦截器
     * <p>
     * 功能说明：
     * - 在SQL执行前拦截并替换表名
     * - 支持通过配置文件动态配置表名映射关系
     * - 可通过enabled开关控制是否启用表名替换功能
     * <p>
     * 使用场景：
     * 1. 分表场景下的动态表名切换
     * 2. 测试环境与生产环境表名差异处理
     * 3. 数据迁移过程中的表名映射
     *
     * @return 动态表名拦截器实例
     */
    private DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor() {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor =
            new DynamicTableNameInnerInterceptor();

        // 设置表名处理器，定义表名替换逻辑
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {

            // 检查是否开启表名替换功能（默认关闭）
            if (!tableReplaceProperties.isEnabled()) {
                log.info("【表名替换】 未开启表名替换功能");
                return tableName;
            }

            // 从配置中获取表名映射关系并进行替换
            Map<String, String> tableNameMap = tableReplaceProperties.getTableNameMap();
            if (tableNameMap != null && tableNameMap.containsKey(tableName)) {
                String newTableName = tableNameMap.get(tableName);
                log.info("【表名替换】 原始表名: {} -> 替换表名: {}", tableName, newTableName);
                return newTableName;
            }

            // 未配置映射关系，返回原始表名
            return tableName;
        });

        return dynamicTableNameInnerInterceptor;
    }
}
