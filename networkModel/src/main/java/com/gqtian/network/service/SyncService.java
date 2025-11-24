package com.gqtian.network.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.gqtian.network.cache.SystemCache;
import com.gqtian.network.config.PlatformProperties;
import com.gqtian.network.entity.UserInfo;
import com.gqtian.network.request.SyncRequest;
import com.gqtian.network.request.entity.RequestHead;
import com.gqtian.network.request.entity.RequestParam;
import com.gqtian.network.request.entity.ResponseResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 同步服务类
 * @className TestService
 * @author gqtian
 * @date 2025/11/24 14:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final PlatformProperties platformProperties;

    /**
     * 查询用户信息（个体）
     *
     */
    public void queryUserInfoObject() {
        String requestUrl = platformProperties.getRequestUrlPrefix() + platformProperties.getUserInfoObject();
        // 构建请求参数
        RequestParam requestParam = RequestParam.builder().build();
        // 构造请求头
        RequestHead headers = RequestHead.builder().authorization(SystemCache.AUTHORIZATION).build();

        // 发起请求
        ResponseResult<UserInfo> result =
            new SyncRequest<UserInfo>(requestUrl, requestParam, JSON.toJSONString(headers), new TypeReference<>() {})
                .get();

        log.info(" 开始查询用户信息（个体），结果: {}", JSON.toJSON(result.getData()));
    }

    /**
     * 查询用户信息（分页）
     *
     */
    public void queryUserInfoPage() {
        String requestUrl = platformProperties.getRequestUrlPrefix() + platformProperties.getUserInfoPage();
        // 构建请求参数
        RequestParam requestParam = RequestParam.builder().build();

        // 构造请求头
        RequestHead headers = RequestHead.builder().authorization(SystemCache.AUTHORIZATION).build();

        // 发起请求
        ResponseResult<List<UserInfo>> resultList = new SyncRequest<List<UserInfo>>(requestUrl, requestParam,
            JSON.toJSONString(headers), new TypeReference<>() {}).get();

        log.info(" 查询查询用户信息（分页）结束，结果总数: {}", resultList.getData().size());

    }

}
