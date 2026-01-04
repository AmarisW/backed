package com.gqtian.common.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.model.GlobalApiOutEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局对外暴露API控制器
 * <p>
 * 提供查询当前应用中所有被@GlobalApiOut注解标记的API接口列表。
 * 常用于开发调试和接口文档查看
 * </p>
 * 
 * @author gqtian
 * @date 2025/8/7 14:44
 */
@Slf4j
@RestController
@RequestMapping("/globalExposed")
@RequiredArgsConstructor
public class GlobalExposedController {

    /**
     * 获取全局开放API列表
     * <p>返回所有被@GlobalApiOut注解标记的接口信息</p>
     * 
     * @return API接口信息列表
     */
    @GetMapping("/list")
    public List<GlobalApiOutEntity> list() {
        return GlobalBeautifyStart.GLOBAL_API_CACHE.stream().toList();
    }

}