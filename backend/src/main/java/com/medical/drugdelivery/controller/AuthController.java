package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.LoginDTO;
import com.medical.drugdelivery.dto.RegisterDTO;
import com.medical.drugdelivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// ============================================================
// 【CRUD Controller】AuthController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/auth
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 登录注册Controller
 * 处理用户登录和注册
 */
@RestController
// 【CRUD】请求 - ()：请求Auth
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 传用户名和密码，验证成功后返回token和用户信息
     */
    // 【CRUD】新增 - login()：新增Auth
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 用户注册
     * 新用户注册，密码会加密存到数据库
     */
    // 【CRUD】新增 - register()：新增Auth
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }
}
