/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.network.request;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.network.request.entity.ResponseResult;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP同步请求工具类
 *
 * @param <T> 响应数据类型
 * @author gqtian
 * @date: 2025/10/21 15:20
 */
@Slf4j
public class SyncRequest<T> {

    /**
     * 请求地址
     */
    private final String url;
    /**
     * 请求参数
     */
    private final Object param;

    /**
     * 请求头
     */
    private final String headers;

    /**
     * 响应数据类型
     */
    private final TypeReference<ResponseResult<T>> typeReference;

    public SyncRequest(String url, Object param, String headers, TypeReference<ResponseResult<T>> typeReference) {
        this.url = url;
        this.param = param;
        this.headers = headers;
        this.typeReference = typeReference;
    }

    /**
     * 发送GET请求
     *
     * @return 响应结果
     */
    public ResponseResult<T> get() {
        return sendRequest(Method.GET);
    }

    /**
     * 发送POST请求
     *
     * @return 响应结果
     */
    public ResponseResult<T> post() {
        return sendRequest(Method.POST);
    }

    /**
     * 发送HTTP请求
     *
     * @param method HTTP方法
     * @return 响应结果
     */
    private ResponseResult<T> sendRequest(Method method) {
        String responseBody = StringUtils.EMPTY;
        HttpRequest request = HttpUtil.createRequest(method, url);
        request.timeout(300000);
        try {
            // 设置请求头
            if (headers != null) {
                Map<String, String> headersMap = JSON.parseObject(headers, new TypeReference<>() {});
                request.headerMap(headersMap, true);
            }

            // 参数处理
            if (param != null) {
                if (method == Method.POST) {
                    request.body(JSON.toJSONString(param));
                } else if (method == Method.GET) {
                    // 将参数转换为Map形式，并添加到URL查询参数中
                    String paramString = JSON.toJSONString(param);
                    String queryParams = HttpUtil.toParams(JSON.parseObject(paramString));
                    request.setUrl(request.getUrl() + "?" + queryParams);
                }
            }
            // 发送请求并处理响应
            HttpResponse response = request.execute();
            responseBody = response.body();
            if (response.isOk()) {
                return JSON.parseObject(responseBody, typeReference);
            } else {
                throw new GlobalException("接口返回异常");
            }
        } finally {
            log.info(" 请求结束, 请求路径 {} 请求方式 {} 请求头 {} 请求参数 {} 响应结果 {}", request.getUrl(), request.getMethod(),
                JSON.toJSON(headers), JSON.toJSON(param), JSON.toJSON(responseBody));
        }
    }
}
