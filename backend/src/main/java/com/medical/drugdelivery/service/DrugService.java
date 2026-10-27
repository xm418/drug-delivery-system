package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.DrugQueryDTO;
import com.medical.drugdelivery.entity.Drug;

import java.util.List;

/**
 * 药品Service接口
 * 药品的增删改查和上下架管理
 */
public interface DrugService {

    /** 分页查询药品列表 */
    PageResult<Drug> page(DrugQueryDTO queryDTO);

    /** 根据id查药品详情 */
    Drug getById(Long id);

    /** 按分类查药品，下单页面选药用 */
    List<Drug> listByCategoryId(Long categoryId);

    /** 新增药品 */
    void add(Drug drug);

    /** 修改药品信息 */
    void update(Drug drug);

    /** 删除药品（逻辑删除） */
    void delete(Long id);
}
