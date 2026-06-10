package com.gqtian.easytrans.example.vo;

import org.dromara.core.trans.anno.Trans;
import org.dromara.core.trans.constant.TransType;
import org.dromara.core.trans.vo.TransPojo;
import com.gqtian.easytrans.enums.UserStatusEnum;

import lombok.Data;

/**
 * ENUM 枚举翻译示例
 *
 * <p>将 Java 枚举的指定属性（如 label）翻译到 ref 字段，无需数据库。</p>
 */
@Data
public class EnumExampleVo implements TransPojo {

    private String scene = "ENUM 枚举翻译";

    /** UserStatusEnum.label -> statusName */
    @Trans(type = TransType.ENUM, key = "label", ref = "statusName")
    private UserStatusEnum status;

    private String statusName;
}
