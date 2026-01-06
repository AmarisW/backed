package com.gqtian.annotation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 测试SaToken控制器
 * @className TestController
 * @author gqtian
 * @date 2026/1/6 15:08
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    public static Map<Integer, String> userLongin = new HashMap<>();

    /**
     * 静态数据
     */
    static {
        userLongin.put(10001, "123456");
        userLongin.put(10002, "123456");
    }

    /**
     * 登录
     */
    @RequestMapping("doLogin")
    public Map<String, Object> doLogin(Integer username, String password) {
        Map<String, Object> result = new HashMap<>();
        if (userLongin.containsKey(username) && userLongin.get(username).equals(password)) {
            StpUtil.login(username);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("tokenName", tokenInfo.getTokenName());
            result.put("tokenValue", tokenInfo.getTokenValue());
            log.info("用户{}登录成功，Token: {}", username, tokenInfo.getTokenValue());
            return result;
        }
        result.put("success", false);
        result.put("message", "登录失败");
        return result;
    }

    /**
     * 查询登录状态
     * @return 登录状态
     */
    @RequestMapping("isLogin")
    public SaTokenInfo isLogin() {
        log.info("当前会话是否登录：{}", StpUtil.isLogin());
        return StpUtil.getTokenInfo();
    }
}
