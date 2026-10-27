package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.Institution;

import java.util.List;

/**
 * 医疗机构Service接口
 * 管理县、乡、村三级医疗机构，机构之间是树形的上下级关系
 */
public interface InstitutionService {

    /** 查所有机构 */
    List<Institution> listAll();

    /** 按级别查机构，1县级 2乡级 3村级 */
    List<Institution> listByLevel(Integer level);

    /** 按上级机构查下级机构 */
    List<Institution> listByParentId(Long parentId);

    /** 分页查询机构列表 */
    PageResult<Institution> page(PageQueryDTO queryDTO, Integer level);

    /** 根据id查机构 */
    Institution getById(Long id);

    /** 新增机构 */
    void add(Institution institution);

    /** 修改机构信息 */
    void update(Institution institution);

    /** 删除机构（下面还有下级机构的不让删） */
    void delete(Long id);
}
