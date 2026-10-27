package com.medical.drugdelivery.service;

import java.util.List;
import java.util.Map;

/**
 * 首页统计的Service接口
 * 首页上那些卡片、图表、表格的数据都是从这里查的
 */
public interface DashboardService {

    /**
     * 首页上面四个统计卡片
     * 药品总数、库存总量、今日订单、待配送任务
     */
    Map<String, Object> getStats();

    /**
     * 最近7天的订单趋势
     * 折线图用，有日期、订单数、订单金额
     */
    Map<String, Object> getOrderTrend();

    /**
     * 药品分类占比
     * 环形图用
     */
    List<Map<String, Object>> getCategoryRatio();

    /**
     * 库存预警列表
     * 首页下面那个表格用，就是库存低于预警阈值的药品
     */
    List<Map<String, Object>> getStockWarning();

    /**
     * 最近的订单
     * 首页"近期订单"表格用，取最近5条
     */
    List<Map<String, Object>> getRecentOrders();

    /**
     * 配送任务的状态统计
     * 待发货、配送中、已送达、已取消各有多少
     */
    Map<String, Object> getDeliveryStats();
}
