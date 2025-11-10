package com.gqtian.contrast.entity.vo;

import java.util.List;

import com.gqtian.common.annotation.GlobalDict;

import lombok.Data;

/**
 * 🤨 字典对象
 * @className DictVo
 * @author gqtian
 * @date 2025/11/8 15:49
 */
@Data
public class DictVo {

    @GlobalDict(type = "test")
    private String code;

    private String codeTxt;

    private DictDetailVo detail;

    private List<DictDetailVo> detailList;
}
