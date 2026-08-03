package com.gqtian.flyway.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.flyway.entity.FlywayDemoUser;
import com.gqtian.flyway.entity.FlywayHistoryRecord;
import com.gqtian.flyway.service.FlywayDemoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Flyway 迁移验证接口
 * <p>
 * 启动后可通过这些接口确认脚本是否已执行、演示表是否有数据。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@RestController
@RequestMapping("/flyway")
@RequiredArgsConstructor
public class FlywayDemoController {

    private final FlywayDemoService flywayDemoService;

    /**
     * 迁移状态总览（历史 + 演示用户）
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return flywayDemoService.migrationStatus();
    }

    /**
     * 查询 flyway_schema_history
     */
    @GetMapping("/history")
    public List<FlywayHistoryRecord> history() {
        return flywayDemoService.listHistory();
    }

    /**
     * 查询 flyway_demo_user（验证 V1~V3 脚本结果）
     */
    @GetMapping("/users")
    public List<FlywayDemoUser> users() {
        return flywayDemoService.listUsers();
    }
}
