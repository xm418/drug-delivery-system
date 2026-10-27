package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.DeliveryQueryDTO;
import com.medical.drugdelivery.entity.DeliveryTask;
import com.medical.drugdelivery.service.DeliveryTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// ============================================================
// 【CRUD Controller】DeliveryTaskController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/deliverytask
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 配送任务管理Controller
 * 配送任务的创建、状态更新、开始配送、完成配送
 */
@RestController
// 【CRUD】请求 - ()：请求DeliveryTask
@RequestMapping("/delivery-tasks")
public class DeliveryTaskController {

    @Autowired
    private DeliveryTaskService deliveryTaskService;

    /**
     * 分页查询配送任务列表
     * 可以按状态、出发机构、到达机构筛选
     */
    // 【CRUD】查询 - page()：查询DeliveryTask
    @GetMapping("/page")
    public Result<PageResult<DeliveryTask>> page(DeliveryQueryDTO queryDTO) {
        PageResult<DeliveryTask> page = deliveryTaskService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询配送任务详情
     */
    // 【CRUD】查询 - getById()：查询DeliveryTask
    @GetMapping("/{id}")
    public Result<DeliveryTask> getById(@PathVariable Long id) {
        DeliveryTask task = deliveryTaskService.getById(id);
        return Result.success(task);
    }

    /**
     * 创建配送任务
     * 订单审核通过后调用这个接口生成配送任务
     * 一个订单可能有多个配送任务（比如先从县到乡，再从乡到村）
     */
    // 【CRUD】新增 - create()：新增DeliveryTask
    @PostMapping
    public Result<Void> create(@RequestParam Long orderId,
                               @RequestParam Long fromInstitutionId,
                               @RequestParam Long toInstitutionId,
                               @RequestParam(required = false) String deliveryPersonName,
                               @RequestParam(required = false) String deliveryPhone,
                               @RequestParam(required = false) String remark) {
        deliveryTaskService.create(orderId, fromInstitutionId, toInstitutionId,
                deliveryPersonName, deliveryPhone, remark);
        return Result.success();
    }

    /**
     * 更新配送状态
     * 0-待发货 1-配送中 2-已完成 3-已取消
     * 后面做路径优化后可能还要加状态
     */
    // 【CRUD】修改 - updateStatus()：修改DeliveryTask
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String remark) {
        deliveryTaskService.updateStatus(id, status, remark);
        return Result.success();
    }

    /**
     * 开始配送
     * 状态从待发货变成配送中，记录发货时间
     */
    // 【CRUD】修改 - startDelivery()：修改DeliveryTask
    @PutMapping("/{id}/start")
    public Result<Void> startDelivery(@PathVariable Long id) {
        deliveryTaskService.startDelivery(id);
        return Result.success();
    }

    /**
     * 完成配送
     * 状态从配送中变成已完成，记录送达时间
     * 同时要给收货方增加库存
     */
    // 【CRUD】修改 - completeDelivery()：修改DeliveryTask
    @PutMapping("/{id}/complete")
    public Result<Void> completeDelivery(@PathVariable Long id) {
        deliveryTaskService.completeDelivery(id);
        return Result.success();
    }
}
