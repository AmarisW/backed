package com.gqtian.common.globalProcess;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.JSON;
import com.gqtian.common.enums.GlobalResultCodeEnum;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 全局响应体处理切面
 * <p>
 * 使用@RestControllerAdvice和ResponseBodyAdvice实现全局响应数据预处理，
 * 将所有Controller返回的数据统一封装为GlobalResult格式
 * </p>
 * <p>
 * 主要功能：
 * <ul>
 *   <li>统一API返回格式</li>
 *   <li>防止重复包装</li>
 *   <li>处理String类型特殊情况</li>
 * </ul>
 * </p>
 * 
 * @author gqtian
 * @date 2025/7/24 17:27
 */

@RestControllerAdvice // @RestControllerAdvice是@RestController注解的增强，可以实现三个方面的功能：全局异常处理 全局数据绑定 全局数据预处理
@RequiredArgsConstructor
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否支持响应体处理
     * 
     * @param returnType 返回类型
     * @param converterType 消息转换器类型
     * @return 总是返回true，表示所有响应都需要处理
     */

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 在响应体写入前进行处理
     * <p>
     * 处理逻辑：
     * <ol>
 *   <li>如果已经是GlobalResult类型，直接返回，防止重复包装</li>
 *   <li>如果是String类型，转换为JSON字符串返回（避免ClassCastException）</li>
 *   <li>其他类型统一封装为GlobalResult</li>
 * </ol>
     * </p>
     *
     * @param body 原始返回数据
     * @param returnType 返回类型
     * @param selectedContentType 选择的内容类型
     * @param selectedConverterType 选择的消息转换器类型
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 处理后的响应数据
     */
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {

        // 防止重复包裹的问题出现 防止全局异常处理后返回的结果（Result）再次被包装
        if (body instanceof GlobalResult) {
            return body;
        }

        // 如果Controller返回的是String，且使用StringHttpMessageConverter，则直接返回
        // 避免StringHttpMessageConverter尝试将GlobalResult对象转换为String时出现ClassCastException
        if (selectedConverterType.isAssignableFrom(StringHttpMessageConverter.class) || body instanceof String) {
            return JSON.toJSONString(GlobalResult.success(GlobalResultCodeEnum.RC100.getMessage(), body));
        }

        return GlobalResult.success(GlobalResultCodeEnum.RC100.getMessage(), body);
    }
}