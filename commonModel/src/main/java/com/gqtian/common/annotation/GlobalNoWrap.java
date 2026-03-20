package com.gqtian.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记不需要被全局响应包装的方法
 * <p>
 * 使用该注解标记的 Controller 方法，其返回值将保持原始格式，
 * 不会被 {@link com.gqtian.common.globalProcess.GlobalResponseBodyAdvice} 包装
 * </p>
 * 
 * <p>
 * 使用示例：
 * <pre>{@code
 * @NoGlobalWrap
 * @GetMapping("/raw")
 * public String getRawData() {
 *     return "原始数据，不会被包装";
 * }
 * }</pre>
 * </p>
 * 
 * @author gqtian
 * @date 2026/3/6
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalNoWrap {
}
