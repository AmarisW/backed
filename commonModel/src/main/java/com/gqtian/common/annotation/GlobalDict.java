package com.gqtian.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全局字典翻译注解
 * <p>
 * 用于标记需要进行字典值翻译的字段，将编码值自动翻译为对应的文本值
 * 翻译后的文本值将被设置到 {字段名}Txt 字段中
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * @GlobalDict(type = "gender")
 * private String sex;  // 字段值："1"
 * private String sexTxt;  // 翻译后："男"
 * </pre>
 * </p>
 *
 * @author gqtian
 */
@Target({ElementType.FIELD}) // 只能用于字段上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，用于反射
@Inherited // 子类继承父类的字典注解
public @interface GlobalDict {

    /**
     * 字典类型
     * <p>指定要使用的字典类型，例如："gender"、"status"等</p>
     * 
     * @return 字典类型编码
     */
    String type() default "";

    /**
     * 字段名称（保留字段，暂未使用）
     * 
     * @return 字段名
     */
    String field() default "";

    /**
     * 空值默认显示文本
     * <p>当字段值为空或找不到对应字典值时显示的默认文本</p>
     * 
     * @return 空值显示文本
     */
    String emptyText() default "";
}
