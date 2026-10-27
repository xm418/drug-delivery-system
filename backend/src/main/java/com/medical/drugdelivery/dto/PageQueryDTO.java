package com.medical.drugdelivery.dto;

import lombok.Data;

/**
 * 分页查询基础参数
 * 其他查询DTO都继承它，自带页码、每页条数和关键字
 * 默认查第1页，每页10条
 */
@Data
public class PageQueryDTO {

    /** 页码，默认第1页 */
    private Long pageNum = 1L;

    /** 每页条数，默认10条 */
    private Long pageSize = 10L;

    /** 搜索关键字，按名称之类的模糊查 */
    private String keyword;
}
