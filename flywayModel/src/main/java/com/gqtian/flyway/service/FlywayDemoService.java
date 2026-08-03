package com.gqtian.flyway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gqtian.flyway.entity.FlywayDemoUser;
import com.gqtian.flyway.entity.FlywayHistoryRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Flyway 迁移结果汇总服务
 * <p>
 * 基于 MyBatis-Plus 查询历史表与演示用户表，验证启动迁移是否落地。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlywayDemoService {

    private final FlywayHistoryRecordService flywayHistoryRecordService;
    private final FlywayDemoUserService flywayDemoUserService;

    /**
     * 查询 Flyway 迁移历史（按 installed_rank 升序）
     *
     * @return 已执行的迁移记录列表
     */
    public List<FlywayHistoryRecord> listHistory() {
        return flywayHistoryRecordService.list(
            Wrappers.<FlywayHistoryRecord>lambdaQuery()
                .orderByAsc(FlywayHistoryRecord::getInstalledRank));
    }

    /**
     * 查询演示用户表全部数据
     *
     * @return 用户列表
     */
    public List<FlywayDemoUser> listUsers() {
        return flywayDemoUserService.list(
            Wrappers.<FlywayDemoUser>lambdaQuery()
                .orderByAsc(FlywayDemoUser::getId));
    }

    /**
     * 汇总迁移状态：历史条数 + 演示用户数
     *
     * @return 状态摘要
     */
    public Map<String, Object> migrationStatus() {
        Map<String, Object> result = new HashMap<>(4);
        result.put("historyCount", flywayHistoryRecordService.count());
        result.put("userCount", flywayDemoUserService.count());
        result.put("history", listHistory());
        result.put("users", listUsers());
        return result;
    }
}
