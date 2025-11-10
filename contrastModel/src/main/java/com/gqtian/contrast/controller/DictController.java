package com.gqtian.contrast.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.utils.GlobalDictUtil;
import com.gqtian.contrast.entity.vo.DictDetailVo;
import com.gqtian.contrast.entity.vo.DictVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 字典控制类
 * @className DictController
 * @author gqtian
 * @date 2025/11/8 15:46
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dict")
public class DictController {

    /**
     * 单个字典翻译
     */
    @RequestMapping("/translate")
    public DictVo translate() {
        DictVo dictVo = new DictVo();
        dictVo.setCode("1");

        DictDetailVo dictDetailVo = new DictDetailVo();
        dictDetailVo.setCode("2");

        List<DictDetailVo> detailList = new ArrayList<>();
        DictDetailVo one = new DictDetailVo();
        one.setCode("3");
        detailList.add(one);
        DictDetailVo two = new DictDetailVo();
        two.setCode("4");
        detailList.add(two);

        dictVo.setDetail(dictDetailVo);
        dictVo.setDetailList(detailList);
        return GlobalDictUtil.translateDic(dictVo);
    }

}
