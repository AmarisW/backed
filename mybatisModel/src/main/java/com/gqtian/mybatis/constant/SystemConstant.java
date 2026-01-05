package com.gqtian.mybatis.constant;

/**
 * 🔔 全局静态变量
 * 
 * @author GQTIAN
 * @date 2025/7/24 17:15
 **/
public class SystemConstant {

    /**
     * 多数据源类型
     */
    public interface DataSourceType {

        /**
         * 主库
         */
        String MASTER = "master";

        /**
         * 从库
         */
        String SLAVE = "slave";
    }

}
