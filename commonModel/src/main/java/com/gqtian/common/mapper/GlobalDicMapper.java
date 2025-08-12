package com.gqtian.common.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gqtian.common.model.GlobalDic;

/**
 * 描述: 字典dao
 *
 * @author gqtian
 */
@Mapper
public interface GlobalDicMapper extends BaseMapper<GlobalDic> {
    
}