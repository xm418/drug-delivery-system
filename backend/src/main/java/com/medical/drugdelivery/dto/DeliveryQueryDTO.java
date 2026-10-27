package com.medical.drugdelivery.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配送任务分页查询参数
 * 可以按状态、订单号、调出/调入机构筛选
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryQueryDTO extends PageQueryDTO {

    /** 任务状态：0待配送 1配送中 2已送达 3已完成 */
    private Integer status;

    /** 关联的订单id */
    private Long orderId;

    /** 调出机构id */
    private Long fromInstitutionId;

    /** 调入机构id */
    private Long toInstitutionId;
}
