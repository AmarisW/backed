package com.gqtian.easytrans.constant;

/**
 * Easy-Trans 模块常量
 */
public final class EasyTransConstant {

    private EasyTransConstant() {
    }

    /**
     * 性别字典，对应 @Trans(type = DICTIONARY, key = "gender")
     */
    public static final String DICT_GENDER = "gender";

    /**
     * 订单状态字典（内存注入，演示字典不必来自数据库）
     */
    public static final String DICT_ORDER_STATUS = "order_status";

    /**
     * AUTO 自定义数据源命名空间，对应 @Trans(type = AUTO_TRANS, key = "teacher")
     */
    public static final String AUTO_TEACHER = "teacher";
}
