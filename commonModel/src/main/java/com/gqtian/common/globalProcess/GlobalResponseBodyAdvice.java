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
 * 🔔 全局数据预处理
 * 
 * @author GQTIAN
 * @date 2025/7/24 17:27
 **/

@RestControllerAdvice // @RestControllerAdvice是@RestController注解的增强，可以实现三个方面的功能：全局异常处理 全局数据绑定 全局数据预处理
@RequiredArgsConstructor
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 在返回之前进行处理
     *
     * @param body 返回的数据
     * @param returnType 返回的类型
     * @param selectedContentType 返回的类型
     * @param selectedConverterType 返回的类型
     * @param request 请求
     * @param response 响应
     * @return 返回的数据
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