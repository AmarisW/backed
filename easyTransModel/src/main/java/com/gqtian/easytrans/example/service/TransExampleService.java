package com.gqtian.easytrans.example.service;

import org.springframework.stereotype.Service;

import com.gqtian.easytrans.example.vo.AutoExampleVo;
import com.gqtian.easytrans.example.vo.DictionaryExampleVo;
import com.gqtian.easytrans.example.vo.EnumExampleVo;
import com.gqtian.easytrans.example.vo.SimpleExampleVo;
import com.gqtian.easytrans.enums.UserStatusEnum;

/**
 * 各翻译类型示例数据构造
 */
@Service
public class TransExampleService {

    public DictionaryExampleVo buildDictionaryExample() {
        DictionaryExampleVo vo = new DictionaryExampleVo();
        vo.setGender("0");
        vo.setOrderStatus("2");
        return vo;
    }

    public SimpleExampleVo buildSimpleExample() {
        SimpleExampleVo vo = new SimpleExampleVo();
        vo.setDeptId(1L);
        vo.setLeaderId(1L);
        return vo;
    }

    public EnumExampleVo buildEnumExample() {
        EnumExampleVo vo = new EnumExampleVo();
        vo.setStatus(UserStatusEnum.DISABLED);
        return vo;
    }

    public AutoExampleVo buildAutoExample() {
        AutoExampleVo vo = new AutoExampleVo();
        vo.setTeacherId(101L);
        return vo;
    }
}
