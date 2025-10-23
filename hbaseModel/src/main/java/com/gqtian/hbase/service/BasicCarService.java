/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gqtian.hbase.entity.GlobalVehicle;
import com.gqtian.hbase.mapper.BasicCarMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Zhangjy @Date 2021/1/12 10:42 @Desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicCarService {

    private final BasicCarMapper basicCarMapper;

    public List<GlobalVehicle> getVehicleList() {
        LambdaQueryWrapper<GlobalVehicle> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GlobalVehicle::getIndustry, "020");
        return basicCarMapper.selectList(lambdaQueryWrapper);
    }
}
