package com.gqtian.mybatis.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.mybatis.entity.SystemLog;
import com.gqtian.mybatis.mapper.SystemLogMapper;
import com.gqtian.mybatis.service.SystemLogService;

/**
* @author gqtia
* @description 针对表【system_logs(系统操作日志表)】的数据库操作Service实现
* @createDate 2025-12-23 15:47:20
*/
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog>
    implements SystemLogService {

}




