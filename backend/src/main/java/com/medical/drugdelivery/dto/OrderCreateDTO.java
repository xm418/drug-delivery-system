package com.medical.drugdelivery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求参数
 * 机构下单订药时提交的数据，一个订单里可以带多种药品（items）
 */
@Data
public class OrderCreateDTO {

    /** 下单的机构id */
    @NotNull(message = "机构ID不能为空")
    private Long institutionId;

    /** 订单备注，选填 */
    private String remark;

    /** 订单明细列表，至少得有一种药 */
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemDTO> items;
}
