package com.medical.drugdelivery.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果包装类
 * 列表查询的接口统一返回这个，里面有总数、当前页数据、页码、每页条数
 * 前端表格分页直接拿这些字段用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    /** 总记录数 */
    private Long total;

    /** 当前页的数据列表 */
    private List<T> records;

    /** 当前页码 */
    private Long pageNum;

    /** 每页条数 */
    private Long pageSize;

    /** 快速构造一个分页结果 */
    public static <T> PageResult<T> of(Long total, List<T> records, Long pageNum, Long pageSize) {
        return new PageResult<>(total, records, pageNum, pageSize);
    }
}
