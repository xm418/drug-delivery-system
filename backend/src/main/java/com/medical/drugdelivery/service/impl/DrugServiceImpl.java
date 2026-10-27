package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.DrugQueryDTO;
import com.medical.drugdelivery.entity.Drug;
import com.medical.drugdelivery.entity.DrugCategory;
import com.medical.drugdelivery.mapper.DrugCategoryMapper;
import com.medical.drugdelivery.mapper.DrugMapper;
import com.medical.drugdelivery.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

// ============================================================
// 【CRUD Service 实现】DrugServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 药品管理Service实现类
 */
@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private DrugCategoryMapper categoryMapper;

    /**
     * 分页查询药品
     * 这里要注意，查出来的药品只有分类id，没有分类名称，需要自己填一下
     */
    @Override
    public PageResult<Drug> page(DrugQueryDTO queryDTO) {
        // 构建分页对象
        Page<Drug> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索：药品名称或编码
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().equals("")) {
            wrapper.and(w -> {
                w.like(Drug::getDrugName, queryDTO.getKeyword());
                w.or().like(Drug::getDrugCode, queryDTO.getKeyword());
            });
        }

        // 按分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Drug::getCategoryId, queryDTO.getCategoryId());
        }

        // 按状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Drug::getStatus, queryDTO.getStatus());
        }

        // 按创建时间倒序排列，最新的在前面
        wrapper.orderByDesc(Drug::getCreateTime);

        // 执行分页查询
        Page<Drug> result = drugMapper.selectPage(page, wrapper);

        // 给每条数据填上分类名称
        // 这里是一条条查的，数据多了可能有点慢，但毕设够用了
        // 以后优化的话可以用联表查询
        for (Drug drug : result.getRecords()) {
            fillCategoryName(drug);
        }

        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据id查询药品详情
     */
    @Override
    public Drug getById(Long id) {
        Drug drug = drugMapper.selectById(id);
        if (drug != null) {
            fillCategoryName(drug);
        }
        return drug;
    }

    /**
     * 根据分类id查询药品列表
     * 只查询上架的药品
     */
    @Override
    public List<Drug> listByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getCategoryId, categoryId);
        wrapper.eq(Drug::getStatus, Constants.DRUG_STATUS_ON_SALE); // 只查上架的
        wrapper.orderByAsc(Drug::getId);
        return drugMapper.selectList(wrapper);
    }

    /**
     * 新增药品
     * 给一些默认值
     */
    @Override
    // 【CRUD】新增：补默认值 → mapper.insert()（MyBatis Plus 自动生成 INSERT）
    public void add(Drug drug) {
        // 默认上架
        if (drug.getStatus() == null) {
            drug.setStatus(Constants.DRUG_STATUS_ON_SALE);
        }
        // 默认预警阈值10，后面可以改
        if (drug.getWarningThreshold() == null) {
            drug.setWarningThreshold(Constants.STOCK_WARNING_THRESHOLD);
        }
        drugMapper.insert(drug);
    }

    /**
     * 修改药品
     */
    @Override
    // 【CRUD】修改：mapper.updateById()（主键 id 必填，自动刷新 update_time）
    public void update(Drug drug) {
        drugMapper.updateById(drug);
    }

    /**
     * 删除药品（逻辑删除）
     * MyBatis Plus的@TableLogic会自动把delete变成update deleted=1
     */
    @Override
    // 【CRUD】删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
    public void delete(Long id) {
        drugMapper.deleteById(id);
    }

    /**
     * 给药品对象填充分类名称
     * 因为drug表里只有categoryId，页面上要显示分类名字
     * TODO: 后面数据多了考虑改成联表查询，性能好点
     */
    private void fillCategoryName(Drug drug) {
        if (drug.getCategoryId() != null) {
            DrugCategory category = categoryMapper.selectById(drug.getCategoryId());
            if (category != null) {
                drug.setCategoryName(category.getCategoryName());
            }
        }
    }
}
