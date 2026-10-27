package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.PasswordDTO;
import com.medical.drugdelivery.dto.UserQueryDTO;
import com.medical.drugdelivery.entity.SysUser;
import com.medical.drugdelivery.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// ============================================================
// 【CRUD Controller】UserController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/user
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 用户管理Controller
 * 用户的增删改查，还有修改密码这些
 */
@RestController
// 【CRUD】请求 - ()：请求User
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    // 【CRUD】查询 - page()：查询User
    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(UserQueryDTO queryDTO) {
        PageResult<SysUser> page = userService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询用户详情
     */
    // 【CRUD】查询 - getById()：查询User
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    // 【CRUD】新增 - add()：新增User
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        userService.add(user);
        return Result.success();
    }

    /**
     * 修改用户信息
     */
    // 【CRUD】修改 - update()：修改User
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        userService.update(user);
        return Result.success();
    }

    /**
     * 删除用户（逻辑删除）
     */
    // 【CRUD】删除 - delete()：删除User
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    /**
     * 修改密码
     * 用户自己改密码，需要输入旧密码
     * userId是从token里拿的，在拦截器里放到request里了
     */
    // 【CRUD】修改 - updatePassword()：修改User
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordDTO passwordDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, passwordDTO);
        return Result.success();
    }

    /**
     * 重置密码
     * 管理员可以重置用户密码，默认重置成123456
     */
    // 【CRUD】修改 - resetPassword()：修改User
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * 前端获取用户信息用的
     */
    // 【CRUD】查询 - getInfo()：查询User
    @GetMapping("/info")
    public Result<SysUser> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = userService.getById(userId);
        // 密码不能返回给前端
        user.setPassword(null);
        return Result.success(user);
    }
}
