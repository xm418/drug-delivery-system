package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// ============================================================
// 【CRUD Controller】DashboardController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/dashboard
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 首页统计Controller
 * 首页上面的卡片、折线图、饼图，还有下面两个表格的数据都从这儿查
 * 这些接口是要登录的，前端请求的时候会带上token
 */
@RestController
// 【CRUD】请求 - ()：请求Dashboard
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 首页四个统计卡片
     * 返回药品总数、库存总量、今日订单、待配送任务
     */
    // 【CRUD】查询 - stats()：查询Dashboard
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = dashboardService.getStats();
        return Result.success(data);
    }

    /**
     * 最近7天的订单趋势，折线图用
     */
    // 【CRUD】查询 - orderTrend()：查询Dashboard
    @GetMapping("/order-trend")
    public Result<Map<String, Object>> orderTrend() {
        Map<String, Object> data = dashboardService.getOrderTrend();
        return Result.success(data);
    }

    /**
     * 药品分类占比，饼图用
     */
    // 【CRUD】查询 - categoryRatio()：查询Dashboard
    @GetMapping("/category-ratio")
    public Result<List<Map<String, Object>>> categoryRatio() {
        List<Map<String, Object>> data = dashboardService.getCategoryRatio();
        return Result.success(data);
    }

    /**
     * 库存预警列表，首页下面的表格用
     */
    // 【CRUD】查询 - stockWarning()：查询Dashboard
    @GetMapping("/stock-warning")
    public Result<List<Map<String, Object>>> stockWarning() {
        List<Map<String, Object>> data = dashboardService.getStockWarning();
        return Result.success(data);
    }

    /**
     * 最近的订单，首页右边那个表格用
     */
    // 【CRUD】查询 - recentOrders()：查询Dashboard
    @GetMapping("/recent-orders")
    public Result<List<Map<String, Object>>> recentOrders() {
        List<Map<String, Object>> data = dashboardService.getRecentOrders();
        return Result.success(data);
    }

    /**
     * 配送任务的状态统计
     * 首页暂时没用到，先留着
     */
    // 【CRUD】查询 - deliveryStats()：查询Dashboard
    @GetMapping("/delivery-stats")
    public Result<Map<String, Object>> deliveryStats() {
        Map<String, Object> data = dashboardService.getDeliveryStats();
        return Result.success(data);
    }
}
