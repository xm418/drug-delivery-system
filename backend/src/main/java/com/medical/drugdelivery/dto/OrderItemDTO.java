package com.medical.drugdelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细项
 * 订单里每一种药对应一条明细，记录药品、数量、单价、小计
 */
@Data
public class OrderItemDTO {

    @NotNull(message = "药品ID不能为空")
    private Long drugId;

    /** 药品名称，下单时冗余存一份，防止以后药品改名订单对不上 */
    private String drugName;

    /** 规格，比如"0.25g*24片" */
    private String specification;

    /** 单位，比如"盒"、"瓶" */
    private String unit;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 小计金额 = 单价 * 数量 */
    private BigDecimal totalPrice;
}
