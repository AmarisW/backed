package com.gqtian.easytrans.example.vo;

import org.dromara.core.trans.anno.Trans;
import org.dromara.core.trans.constant.TransType;
import org.dromara.core.trans.vo.TransPojo;
import com.gqtian.easytrans.constant.EasyTransConstant;

import lombok.Data;

/**
 * DICTIONARY 字典翻译示例
 *
 * <p>字典数据由 {@link com.gqtian.easytrans.config.EasyTransDictInitRunner} 启动时加载：</p>
 * <ul>
 *   <li>gender — 来自 sys_dict 表</li>
 *   <li>order_status — 内存 Map 注入，无需数据库</li>
 * </ul>
 */
@Data
public class DictionaryExampleVo implements TransPojo {

    private String scene = "DICTIONARY 字典翻译";

    /** 性别：0 -> 男，1 -> 女 */
    @Trans(type = TransType.DICTIONARY, key = EasyTransConstant.DICT_GENDER, ref = "genderName")
    private String gender;

    private String genderName;

    /** 订单状态：1 -> 待支付，2 -> 已完成（纯内存字典） */
    @Trans(type = TransType.DICTIONARY, key = EasyTransConstant.DICT_ORDER_STATUS, ref = "orderStatusName")
    private String orderStatus;

    private String orderStatusName;
}
