package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// 【CRUD Controller】InstitutionController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/institution
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 医疗机构管理Controller
 * 县、乡、村三级医疗机构的管理
 */
@RestController
// 【CRUD】请求 - ()：请求Institution
@RequestMapping("/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    /**
     * 查询所有医疗机构列表
     * 下拉选的时候用
     */
    // 【CRUD】查询 - listAll()：查询Institution
    @GetMapping("/list")
    public Result<List<Institution>> listAll() {
        List<Institution> list = institutionService.listAll();
        return Result.success(list);
    }

    /**
     * 按级别查询医疗机构
     * level=1县级 2乡镇 3村卫生室
     */
    // 【CRUD】查询 - listByLevel()：查询Institution
    @GetMapping("/list/level/{level}")
    public Result<List<Institution>> listByLevel(@PathVariable Integer level) {
        List<Institution> list = institutionService.listByLevel(level);
        return Result.success(list);
    }

    /**
     * 按上级机构查询子机构列表
     * 比如查某个乡镇下面的所有村卫生室
     */
    // 【CRUD】查询 - listByParentId()：查询Institution
    @GetMapping("/list/parent/{parentId}")
    public Result<List<Institution>> listByParentId(@PathVariable Long parentId) {
        List<Institution> list = institutionService.listByParentId(parentId);
        return Result.success(list);
    }

    /**
     * 分页查询医疗机构
     */
    // 【CRUD】查询 - page()：查询Institution
    @GetMapping("/page")
    public Result<PageResult<Institution>> page(PageQueryDTO queryDTO,
                                                @RequestParam(required = false) Integer level) {
        PageResult<Institution> page = institutionService.page(queryDTO, level);
        return Result.success(page);
    }

    /**
     * 根据id查询机构详情
     */
    // 【CRUD】查询 - getById()：查询Institution
    @GetMapping("/{id}")
    public Result<Institution> getById(@PathVariable Long id) {
        Institution institution = institutionService.getById(id);
        return Result.success(institution);
    }

    /**
     * 新增医疗机构
     */
    // 【CRUD】新增 - add()：新增Institution
    @PostMapping
    public Result<Void> add(@RequestBody Institution institution) {
        institutionService.add(institution);
        return Result.success();
    }

    /**
     * 修改医疗机构信息
     */
    // 【CRUD】修改 - update()：修改Institution
    @PutMapping
    public Result<Void> update(@RequestBody Institution institution) {
        institutionService.update(institution);
        return Result.success();
    }

    /**
     * 删除医疗机构
     * 注意：删除前要检查下面有没有子机构，如果有就不能删
     * TODO: 后面要加这个判断
     */
    // 【CRUD】删除 - delete()：删除Institution
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        institutionService.delete(id);
        return Result.success();
    }
}
