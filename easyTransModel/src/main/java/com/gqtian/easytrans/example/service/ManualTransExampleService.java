package com.gqtian.easytrans.example.service;

import java.util.List;

import org.dromara.core.trans.anno.TransMethodResult;
import org.dromara.trans.service.impl.TransService;
import org.springframework.stereotype.Service;

import com.gqtian.easytrans.enums.UserStatusEnum;
import com.gqtian.easytrans.example.vo.DictionaryExampleVo;
import com.gqtian.easytrans.example.vo.EnumExampleVo;

import lombok.RequiredArgsConstructor;

/**
 * 手动翻译示例
 *
 * <p>全局翻译（is-enable-global=true）关闭时，或需要在 Service 层提前翻译时使用。</p>
 */
@Service
@RequiredArgsConstructor
public class ManualTransExampleService {

    private final TransService transService;
    private final TransExampleService transExampleService;

    /**
     * 方式一：注入 TransService，主动调用 transOne / transBatch
     */
    public DictionaryExampleVo translateByTransService() {
        DictionaryExampleVo vo = transExampleService.buildDictionaryExample();
        transService.transOne(vo);
        return vo;
    }

    public List<EnumExampleVo> translateBatchByTransService() {
        List<EnumExampleVo> list = List.of(
            buildEnum(UserStatusEnum.NORMAL),
            buildEnum(UserStatusEnum.DISABLED));
        transService.transBatch(list);
        return list;
    }

    /**
     * 方式二：@TransMethodResult 标记方法，AOP 自动翻译返回值
     */
    @TransMethodResult
    public DictionaryExampleVo translateByMethodResult() {
        return transExampleService.buildDictionaryExample();
    }

    private EnumExampleVo buildEnum(UserStatusEnum status) {
        EnumExampleVo vo = new EnumExampleVo();
        vo.setStatus(status);
        return vo;
    }
}
