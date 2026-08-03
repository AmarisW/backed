package com.gqtian.flyway.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.flyway.entity.FlywayHistoryRecord;
import com.gqtian.flyway.mapper.FlywayHistoryRecordMapper;
import com.gqtian.flyway.service.FlywayHistoryRecordService;

/**
 * Flyway 迁移历史 Service 实现
 *
 * @author gqtian
 */
@Service
public class FlywayHistoryRecordServiceImpl
    extends ServiceImpl<FlywayHistoryRecordMapper, FlywayHistoryRecord>
    implements FlywayHistoryRecordService {
}
