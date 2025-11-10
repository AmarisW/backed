package com.gqtian.contrast.entity.vo;

import com.gqtian.common.annotation.GlobalDict;

import lombok.Data;

/**
 * 🤨 
 * @className DictDetailVo
 * @author gqtian
 * @date 2025/11/8 15:51
 */
@Data
public class DictDetailVo {

    @GlobalDict(type = "test")
    private String code;

    private String codeTxt;

}
