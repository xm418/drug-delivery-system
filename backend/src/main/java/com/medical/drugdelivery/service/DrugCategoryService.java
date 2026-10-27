package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.DrugCategory;

import java.util.List;

/**
 * 药品分类Service接口
 * 分类是有层级的（父分类下面挂子分类），药品管理里按分类筛选用
 */
public interface DrugCategoryService {

    /** 查所有分类，前端下拉框/树形选择用 */
    List<DrugCategory> listAll();

    /** 按父级id查子分类 */
    List<DrugCategory> listByParentId(Long parentId);

    /** 分页查询分类列表 */
    PageResult<DrugCategory> page(PageQueryDTO queryDTO);

    /** 根据id查分类 */
    DrugCategory getById(Long id);

    /** 新增分类 */
    void add(DrugCategory category);

    /** 修改分类 */
    void update(DrugCategory category);

    /** 删除分类（有药品挂在下面的分类不让删） */
    void delete(Long id);
}
