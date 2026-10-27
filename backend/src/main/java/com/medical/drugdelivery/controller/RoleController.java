package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.PageQueryDTO;
import com.medical.drugdelivery.entity.SysRole;
import com.medical.drugdelivery.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// 【CRUD Controller】RoleController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/role
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 角色管理Controller
 * 角色的增删改查
 * 目前角色比较简单，就是管理员、乡镇用户、村医这些
 */
@RestController
// 【CRUD】请求 - ()：请求Role
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 查询所有角色列表
     * 用户管理页面的角色下拉选用
     */
    // 【CRUD】查询 - listAll()：查询Role
    @GetMapping("/list")
    public Result<List<SysRole>> listAll() {
        List<SysRole> list = roleService.listAll();
        return Result.success(list);
    }

    /**
     * 分页查询角色
     */
    // 【CRUD】查询 - page()：查询Role
    @GetMapping("/page")
    public Result<PageResult<SysRole>> page(PageQueryDTO queryDTO) {
        PageResult<SysRole> page = roleService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询角色详情
     */
    // 【CRUD】查询 - getById()：查询Role
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    // 【CRUD】新增 - add()：新增Role
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.add(role);
        return Result.success();
    }

    /**
     * 修改角色
     */
    // 【CRUD】修改 - update()：修改Role
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        roleService.update(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    // 【CRUD】删除 - delete()：删除Role
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }
}
