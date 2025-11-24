package com.gqtian.network.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.network.service.SyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 同步服务类
 * @className TestService
 * @author gqtian
 * @date 2025/11/24 14:44
 */
@Slf4j
@RestController
@RequestMapping("sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    /**
     * 查询用户信息（个体）
     */
    @RequestMapping("queryUserInfoObject")
    public void queryUserInfoObject() {
        syncService.queryUserInfoObject();
    }

    /**
     * 查询用户信息（分页）
     */
    @RequestMapping("queryUserInfoPage")
    public void queryUserInfoPage() {
        syncService.queryUserInfoPage();
    }
}
