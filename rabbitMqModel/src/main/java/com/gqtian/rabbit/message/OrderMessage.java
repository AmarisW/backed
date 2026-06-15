package com.gqtian.rabbit.message;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单消息体
 *
 * @author gqtian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单 ID */
    private String orderId;

    /** 用户 ID */
    private Long userId;

    /** 订单金额 */
    private BigDecimal amount;

    /** 订单状态：CREATED / PAID / SHIPPED / FINISHED */
    private String status;

    /** 消息产生时间 */
    private LocalDateTime createTime;
}
