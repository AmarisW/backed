package com.gqtian.network.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.gqtian.network.cache.SystemCache;
import com.gqtian.network.config.CollectProperties;
import com.gqtian.network.entity.UserInfo;
import com.gqtian.network.request.SyncRequest;
import com.gqtian.network.request.entity.RequestHead;
import com.gqtian.network.request.entity.RequestParam;
import com.gqtian.network.request.entity.ResponseResult;
import com.gqtian.network.utils.CommonUtil;

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

    private final CollectProperties collectProperties;
    private final UserInfoRepository userInfoRepository;

    /**
     * 查询用户信息（个体）
     *
     */
    public void queryUserInfoObject() {
        String requestUrl = collectProperties.getUrlPrefix() + collectProperties.getUserInfoObject();

        // 构建请求参数
        RequestParam requestParam = RequestParam.builder().build();

        // 构造请求头
        RequestHead headers = RequestHead.builder().authorization(SystemCache.AUTHORIZATION).build();

        // 发起请求
        ResponseResult<UserInfo> result =
            new SyncRequest<UserInfo>(requestUrl, requestParam, JSON.toJSONString(headers), new TypeReference<>() {})
                .get();

        UserInfo userInfo = result.getData();
        log.info(" 开始查询用户信息（个体），结果: {}", JSON.toJSON(userInfo));

        // 开始保存
        CommonUtil.saveBatch("保存用户信息", Collections.singletonList(userInfo),
            (dataList) -> userInfoRepository.saveBatch(dataList, 500));

    }

    /**
     * 查询用户信息（分页）
     *
     */
    public void queryUserInfoPage() {
        String requestUrl = collectProperties.getUrlPrefix() + collectProperties.getUserInfoPage();

        // 构建请求参数
        RequestParam requestParam = RequestParam.builder().build();

        // 构造请求头
        RequestHead headers = RequestHead.builder().authorization(SystemCache.AUTHORIZATION).build();

        // 发起分页请求
        List<UserInfo> userInfos = CommonUtil.fetchDataWithPage(requestParam,
            (param, requestHead) -> new SyncRequest<List<UserInfo>>(requestUrl, param, JSON.toJSONString(headers),
                new TypeReference<>() {}).get());

        log.info(" 查询查询用户信息（分页）结束，结果总数: {}", userInfos.size());

        // 批量保存
        CommonUtil.saveBatch("保存用户信息", userInfos, (dataList) -> userInfoRepository.saveBatch(dataList, 500));

    }

}
