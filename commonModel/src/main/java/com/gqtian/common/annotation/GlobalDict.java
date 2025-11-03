package com.gqtian.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解翻译： TODO:需要手动添加翻译后的字段名称,可优化为自动添加翻译字段（以..Txt结尾）
 */
@Target({ElementType.FIELD}) // 使用范围
@Retention(RetentionPolicy.RUNTIME) // 生命周期
@Inherited // 如果父类使用了CodeToValue注解，则子类应该继承
public @interface GlobalDict {

    /**
     * 字典type
     */
    String type() default "";

    /**
     * 字段
     */
    String field() default "";

    /**
     * emptyText:空值默认显示.
     */
    String emptyText() default "";
}
