package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.DrugQueryDTO;
import com.medical.drugdelivery.entity.Drug;
import com.medical.drugdelivery.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// 【CRUD Controller】DrugController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/drug
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 药品管理Controller
 * 主要就是增删改查，还有分页查询
 */
@RestController
// 【CRUD】请求 - ()：请求Drug
@RequestMapping("/drugs")
public class DrugController {

    // 用@Autowired注入，比构造器简单点
    @Autowired
    private DrugService drugService;

    /**
     * 分页查询药品列表
     * 支持按名称/编码模糊搜索，按分类、状态筛选
     */
    // 【CRUD】查询 - page()：查询Drug
    @GetMapping("/page")
    public Result<PageResult<Drug>> page(DrugQueryDTO queryDTO) {
        // System.out.println("查询参数：" + queryDTO); // 调试用的，先注释了
        PageResult<Drug> page = drugService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询药品详情
     */
    // 【CRUD】查询 - getById()：查询Drug
    @GetMapping("/{id}")
    public Result<Drug> getById(@PathVariable Long id) {
        Drug drug = drugService.getById(id);
        return Result.success(drug);
    }

    /**
     * 根据分类id查询药品列表
     * 下拉选的时候用
     */
    // 【CRUD】查询 - listByCategoryId()：查询Drug
    @GetMapping("/category/{categoryId}")
    public Result<List<Drug>> listByCategoryId(@PathVariable Long categoryId) {
        List<Drug> list = drugService.listByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 新增药品
     */
    // 【CRUD】新增 - add()：新增Drug
    @PostMapping
    public Result<Void> add(@RequestBody Drug drug) {
        drugService.add(drug);
        return Result.success();
    }

    /**
     * 修改药品
     */
    // 【CRUD】修改 - update()：修改Drug
    @PutMapping
    public Result<Void> update(@RequestBody Drug drug) {
        drugService.update(drug);
        return Result.success();
    }

    /**
     * 删除药品（逻辑删除，不是真删）
     */
    // 【CRUD】删除 - delete()：删除Drug
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        drugService.delete(id);
        return Result.success();
    }
}
