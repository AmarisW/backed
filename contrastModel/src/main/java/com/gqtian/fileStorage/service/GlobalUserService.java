package com.gqtian.fileStorage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gqtian.fileStorage.demo.excel.PageQuerySupplier;
import com.gqtian.fileStorage.entity.GlobalUser;
import com.gqtian.fileStorage.mapper.GlobalUserMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻
 * 
 * @className UserService
 * @author gqtian
 * @date 2025/8/14 23:53
 */
@Slf4j
@Service
public class GlobalUserService extends ServiceImpl<GlobalUserMapper, GlobalUser>
    implements PageQuerySupplier<GlobalUser> {
    
    @Override
    public List<GlobalUser> getPage(int pageNum, int pageSize) {
        return this.list(new Page<>(pageNum, pageSize));
    }
}
