package com.gqtian.network.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.network.entity.UserInfo;
import com.gqtian.network.service.SyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 用户控制类
 * @className TestService
 * @author gqtian
 * @date 2025/11/24 14:44
 */
@Slf4j
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserInfoController {

    private final SyncService syncService;

    /**
     * 查询用户信息（个体）
     */
    @RequestMapping("queryUserInfoObject")
    public UserInfo queryUserInfoObject() {
        return UserInfo.builder().name("张三").age(18).build();
    }

    /**
     * 查询用户信息（分页）
     */
    @RequestMapping("queryUserInfoPage")
    public List<UserInfo> queryUserInfoPage() {
        List<UserInfo> list = new ArrayList<>();
        list.add(UserInfo.builder().name("张三").age(18).build());
        list.add(UserInfo.builder().name("李四").age(19).build());
        list.add(UserInfo.builder().name("王五").age(20).build());
        return list;
    }
}
