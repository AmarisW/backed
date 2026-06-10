package com.gqtian.easytrans.example.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dromara.core.trans.anno.IgnoreTrans;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.easytrans.example.service.ManualTransExampleService;
import com.gqtian.easytrans.example.service.TransExampleService;
import com.gqtian.easytrans.example.vo.AutoExampleVo;
import com.gqtian.easytrans.example.vo.DictionaryExampleVo;
import com.gqtian.easytrans.example.vo.EnumExampleVo;
import com.gqtian.easytrans.example.vo.SimpleExampleVo;

import lombok.RequiredArgsConstructor;

/**
 * Easy-Trans 翻译类型示例（不含 RPC）
 *
 * <p>启动后按类型访问（端口与 context-path 见 application.yml）：</p>
 * <ul>
 *   <li>GET /examples/dictionary — 字典翻译</li>
 *   <li>GET /examples/simple — 简单翻译（需 MySQL）</li>
 *   <li>GET /examples/enum — 枚举翻译</li>
 *   <li>GET /examples/auto — 自定义数据源翻译</li>
 *   <li>GET /examples/manual/trans-service — TransService 手动翻译</li>
 *   <li>GET /examples/manual/method-result — @TransMethodResult 翻译</li>
 *   <li>GET /examples/manual/ignore — @IgnoreTrans 跳过翻译</li>
 *   <li>GET /examples/routes — 路由说明</li>
 * </ul>
 */
@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class TransExampleController {

    private final TransExampleService transExampleService;
    private final ManualTransExampleService manualTransExampleService;

    /**
     * DICTIONARY：字典码 -> 中文标签
     */
    @GetMapping("/dictionary")
    public DictionaryExampleVo dictionary() {
        return transExampleService.buildDictionaryExample();
    }

    /**
     * SIMPLE：ID 自动查表翻译（需数据库 sys_dept、sys_user）
     */
    @GetMapping("/simple")
    public SimpleExampleVo simple() {
        return transExampleService.buildSimpleExample();
    }

    /**
     * ENUM：枚举属性 -> 展示文本
     */
    @GetMapping("/enum")
    public EnumExampleVo enumExample() {
        return transExampleService.buildEnumExample();
    }

    /**
     * AUTO_TRANS：自定义数据源翻译（内存讲师数据，无需数据库）
     */
    @GetMapping("/auto")
    public AutoExampleVo auto() {
        return transExampleService.buildAutoExample();
    }

    /**
     * 手动翻译：TransService.transOne
     */
    @GetMapping("/manual/trans-service")
    public DictionaryExampleVo manualTransService() {
        return manualTransExampleService.translateByTransService();
    }

    /**
     * 手动翻译：TransService.transBatch
     */
    @GetMapping("/manual/trans-batch")
    public List<EnumExampleVo> manualTransBatch() {
        return manualTransExampleService.translateBatchByTransService();
    }

    /**
     * 手动翻译：@TransMethodResult
     */
    @GetMapping("/manual/method-result")
    public DictionaryExampleVo manualMethodResult() {
        return manualTransExampleService.translateByMethodResult();
    }

    /**
     * 关闭全局翻译：@IgnoreTrans（genderName 等 ref 字段不会被填充）
     */
    @IgnoreTrans
    @GetMapping("/manual/ignore")
    public DictionaryExampleVo ignoreTrans() {
        return transExampleService.buildDictionaryExample();
    }

    @GetMapping("/routes")
    public Map<String, Object> routes() {
        Map<String, Object> routes = new LinkedHashMap<>();
        routes.put("dictionary", "GET /examples/dictionary — 字典翻译 DICTIONARY");
        routes.put("simple", "GET /examples/simple — 简单翻译 SIMPLE（需 MySQL）");
        routes.put("enum", "GET /examples/enum — 枚举翻译 ENUM");
        routes.put("auto", "GET /examples/auto — 自定义翻译 AUTO_TRANS");
        routes.put("manualTransService", "GET /examples/manual/trans-service — TransService.transOne");
        routes.put("manualTransBatch", "GET /examples/manual/trans-batch — TransService.transBatch");
        routes.put("manualMethodResult", "GET /examples/manual/method-result — @TransMethodResult");
        routes.put("ignoreTrans", "GET /examples/manual/ignore — @IgnoreTrans 跳过翻译");
        routes.put("users", "GET /users — 综合示例（四种翻译合一）");
        return routes;
    }
}
