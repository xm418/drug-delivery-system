package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.DrugCategory;
import com.medical.drugdelivery.service.DrugCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// 【CRUD Controller】DrugCategoryController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/drugcategory
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 药品分类管理Controller
 * 药品分类的增删改查
 */
@RestController
// 【CRUD】请求 - ()：请求DrugCategory
@RequestMapping("/drug-categories")
public class DrugCategoryController {

    @Autowired
    private DrugCategoryService drugCategoryService;

    /**
     * 查询所有分类列表
     * 下拉选用
     */
    // 【CRUD】查询 - listAll()：查询DrugCategory
    @GetMapping("/list")
    public Result<List<DrugCategory>> listAll() {
        List<DrugCategory> list = drugCategoryService.listAll();
        return Result.success(list);
    }

    /**
     * 按父分类查询子分类
     * 目前只做了一级分类，以后可能要支持多级
     */
    // 【CRUD】查询 - listByParentId()：查询DrugCategory
    @GetMapping("/list/parent/{parentId}")
    public Result<List<DrugCategory>> listByParentId(@PathVariable Long parentId) {
        List<DrugCategory> list = drugCategoryService.listByParentId(parentId);
        return Result.success(list);
    }

    /**
     * 分页查询分类
     */
    // 【CRUD】查询 - page()：查询DrugCategory
    @GetMapping("/page")
    public Result<PageResult<DrugCategory>> page(PageQueryDTO queryDTO) {
        PageResult<DrugCategory> page = drugCategoryService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询分类详情
     */
    // 【CRUD】查询 - getById()：查询DrugCategory
    @GetMapping("/{id}")
    public Result<DrugCategory> getById(@PathVariable Long id) {
        DrugCategory category = drugCategoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 新增分类
     */
    // 【CRUD】新增 - add()：新增DrugCategory
    @PostMapping
    public Result<Void> add(@RequestBody DrugCategory category) {
        drugCategoryService.add(category);
        return Result.success();
    }

    /**
     * 修改分类
     */
    // 【CRUD】修改 - update()：修改DrugCategory
    @PutMapping
    public Result<Void> update(@RequestBody DrugCategory category) {
        drugCategoryService.update(category);
        return Result.success();
    }

    /**
     * 删除分类
     * 注意：分类下面有药品的话不能删，要先把药品移走或者删掉
     * TODO: 后面加校验
     */
    // 【CRUD】删除 - delete()：删除DrugCategory
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        drugCategoryService.delete(id);
        return Result.success();
    }
}
