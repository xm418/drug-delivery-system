package com.medical.drugdelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存操作请求参数
 * 入库、出库、调拨都用这个，靠operationType区分是哪种操作
 * 调拨的时候才会用到fromInstitutionId和toInstitutionId
 */
@Data
public class StockOperationDTO {

    @NotNull(message = "药品ID不能为空")
    private Long drugId;

    @NotNull(message = "机构ID不能为空")
    private Long institutionId;

    /** 操作类型：1入库 2出库 3调拨，见Constants里的常量 */
    @NotNull(message = "操作类型不能为空")
    private Integer operationType;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /** 单价，入库时记一下成本 */
    private BigDecimal unitPrice;

    /** 药品批号 */
    private String batchNumber;

    /** 生产日期 */
    private LocalDateTime productionDate;

    /** 有效期至 */
    private LocalDateTime expiryDate;

    /** 备注 */
    private String remark;

    /** 调拨时的调出机构 */
    private Long fromInstitutionId;

    /** 调拨时的调入机构 */
    private Long toInstitutionId;
}
