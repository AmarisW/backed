package com.gqtian.common.constant;

/**
 * 全局常量类
 * <p>定义全局公共常量，供整个应用系统使用</p>
 * 
 * @author gqtian
 * @date 2025/7/24 17:15
 */
public class GlobalConstant {

    /**
     * 日期时间格式化常量
     * <p>定义常用的日期时间格式化模式</p>
     */
    public interface DATE_FORMAT_TYPE {

        /** 日期格式：yyyy-MM-dd */
        String DATE = "yyyy-MM-dd";

        /** 紧凑日期格式：yyyyMMdd */
        String DATE_COMPACT = "yyyyMMdd";

        /** 时间格式：HH:mm:ss */
        String TIME = "HH:mm:ss";

        /** 日期时间格式：yyyy-MM-dd HH:mm:ss */
        String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

        /** 紧凑日期时间格式：yyyyMMddHHmmss */
        String DATE_TIME_COMPACT = "yyyyMMddHHmmss";
    }

}
