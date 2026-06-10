package com.gqtian.easytrans.example.vo;

import org.dromara.core.trans.anno.Trans;
import org.dromara.core.trans.constant.TransType;
import org.dromara.core.trans.vo.TransPojo;
import com.gqtian.easytrans.constant.EasyTransConstant;

import lombok.Data;

/**
 * AUTO_TRANS 自定义数据源翻译示例
 *
 * <p>数据源由 {@link com.gqtian.easytrans.example.auto.TeacherAutoTransProvider} 提供，</p>
 * <p>不经过 MyBatis-Plus 查表，适合外部 API、缓存、静态配置等场景。</p>
 */
@Data
public class AutoExampleVo implements TransPojo {

    private String scene = "AUTO_TRANS 自定义数据源翻译";

    /**
     * 讲师 ID -> teacherName、teacherTitle
     * namespace 与 @AutoTrans(namespace = "teacher") 对应
     */
    @Trans(type = TransType.AUTO_TRANS, key = EasyTransConstant.AUTO_TEACHER,
        refs = {"teacherName", "teacherTitle"})
    private Long teacherId;

    private String teacherName;

    private String teacherTitle;
}
