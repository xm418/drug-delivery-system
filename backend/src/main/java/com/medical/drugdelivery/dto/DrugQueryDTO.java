package com.medical.drugdelivery.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 药品分页查询参数
 * 可以按分类、上下架状态筛选，关键字按药品名模糊查
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrugQueryDTO extends PageQueryDTO {

    /** 药品分类id */
    private Long categoryId;

    /** 状态：1在售 0下架 */
    private Integer status;
}
