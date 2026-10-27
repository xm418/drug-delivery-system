package com.medical.drugdelivery.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存分页查询参数
 * 可以按药品、机构筛选，warningOnly为true时只查库存不足的
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockQueryDTO extends PageQueryDTO {

    /** 药品id */
    private Long drugId;

    /** 机构id */
    private Long institutionId;

    /** 是否只看库存预警的（库存量低于阈值的） */
    private Boolean warningOnly;
}
