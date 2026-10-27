package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.OrderCreateDTO;
import com.medical.drugdelivery.dto.OrderQueryDTO;
import com.medical.drugdelivery.entity.DrugOrder;
import com.medical.drugdelivery.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// ============================================================
// 【CRUD Controller】OrderController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/order
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 订单管理Controller
 * 订单的创建、审核、取消、查询
 */
@RestController
// 【CRUD】请求 - ()：请求Order
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     * 支持按订单号、状态、下单机构等筛选
     */
    // 【CRUD】查询 - page()：查询Order
    @GetMapping("/page")
    public Result<PageResult<DrugOrder>> page(OrderQueryDTO queryDTO) {
        PageResult<DrugOrder> page = orderService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询订单基本信息
     */
    // 【CRUD】查询 - getById()：查询Order
    @GetMapping("/{id}")
    public Result<DrugOrder> getById(@PathVariable Long id) {
        DrugOrder order = orderService.getById(id);
        return Result.success(order);
    }

    /**
     * 查询订单详情（包含订单项）
     * 订单详情页用的，把订单药品明细也查出来
     */
    // 【CRUD】查询 - getDetail()：查询Order
    @GetMapping("/{id}/detail")
    public Result<DrugOrder> getDetail(@PathVariable Long id) {
        DrugOrder order = orderService.getDetail(id);
        return Result.success(order);
    }

    /**
     * 创建订单
     * 村卫生室或者乡镇卫生院下单用的
     * userId从token里拿
     */
    // 【CRUD】新增 - create()：新增Order
    @PostMapping
    public Result<Void> create(@RequestBody OrderCreateDTO createDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.create(createDTO, userId);
        return Result.success();
    }

    /**
     * 审核订单
     * approved=1通过，approved=0驳回
     * 审核通过后会生成配送任务
     */
    // 【CRUD】修改 - approve()：修改Order
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id,
                                @RequestParam Integer approved,
                                @RequestParam(required = false) String remark,
                                HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        orderService.approve(id, approved, remark, operatorId);
        return Result.success();
    }

    /**
     * 取消订单
     * 只有待审核的订单才能取消
     */
    // 【CRUD】修改 - cancel()：修改Order
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancel(id, userId);
        return Result.success();
    }
}
