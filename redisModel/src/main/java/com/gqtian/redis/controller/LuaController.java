package com.gqtian.redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.redis.service.LuaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 lua脚本控制器
 * @className LuaController
 * @author gqtian
 * @date 2025/12/19 16:32
 */
@Slf4j
@RestController
@RequestMapping("/lua")
@RequiredArgsConstructor
public class LuaController {

    private final LuaService luaService;

    @RequestMapping("/run")
    public String run(String goodsId, String orderId, String userId) {
        return luaService.runLuaScript(goodsId, orderId, userId);
    }
}
