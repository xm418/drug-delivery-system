package com.medical.drugdelivery.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单分页查询参数
 * 可以按订单状态、下单机构、订单编号筛选
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQueryDTO extends PageQueryDTO {

    /** 订单状态：0待审核 1已审核 2已驳回 3配送中 4已完成 5已取消 */
    private Integer status;

    /** 下单机构id */
    private Long institutionId;

    /** 订单编号 */
    private String orderNo;
}
