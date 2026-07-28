package com.gqtian.rabbit.message;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单消息体（JSON 传输）。
 * <p>
 * 由 {@code Jackson2JsonMessageConverter} 序列化/反序列化，
 * 无需实现 {@link java.io.Serializable}。
 * </p>
 *
 * @author gqtian
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {

    /** 订单 ID */
    private String orderId;

    /** 用户 ID */
    private Long userId;

    /** 订单金额 */
    private BigDecimal amount;

    /**
     * 订单状态。
     * <p>约定：CREATED / PAID / SHIPPED / FINISHED</p>
     */
    private String status;

    /** 消息产生时间 */
    private LocalDateTime createTime;
}
