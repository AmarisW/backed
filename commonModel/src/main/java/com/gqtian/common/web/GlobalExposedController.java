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
 * 👻 开放Api
 * 
 * @className TestWeb
 * @author gqtian
 * @date 2025/8/7 14:44
 */
@Slf4j
@RestController
@RequestMapping("/globalExposed")
@RequiredArgsConstructor
public class GlobalExposedController {
    
    /**
     * 全局开放Api列表
     */
    @GetMapping("/list")
    public List<GlobalApiOutEntity> list() {
        return GlobalBeautifyStart.GLOBAL_API_CACHE.stream().toList();
    }
    
}