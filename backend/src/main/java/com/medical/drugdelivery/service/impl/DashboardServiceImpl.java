package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.drugdelivery.entity.DeliveryTask;
import com.medical.drugdelivery.entity.Drug;
import com.medical.drugdelivery.entity.DrugCategory;
import com.medical.drugdelivery.entity.DrugOrder;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.entity.Stock;
import com.medical.drugdelivery.mapper.DeliveryTaskMapper;
import com.medical.drugdelivery.mapper.DrugCategoryMapper;
import com.medical.drugdelivery.mapper.DrugMapper;
import com.medical.drugdelivery.mapper.DrugOrderMapper;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.mapper.StockMapper;
import com.medical.drugdelivery.service.DashboardService;
import com.medical.drugdelivery.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ============================================================
// 【CRUD Service 实现】DashboardServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 首页统计的实现类
 * 首页要的数据都在这儿查，写的比较直白，基本就是查出来再在Java里算
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    // 药品
    @Autowired
    private DrugMapper drugMapper;

    // 库存
    @Autowired
    private StockMapper stockMapper;

    // 订单
    @Autowired
    private DrugOrderMapper drugOrderMapper;

    // 配送任务
    @Autowired
    private DeliveryTaskMapper deliveryTaskMapper;

    // 药品分类
    @Autowired
    private DrugCategoryMapper drugCategoryMapper;

    // 医疗机构
    @Autowired
    private InstitutionMapper institutionMapper;

    // 库存的Service，首页的库存预警直接用它现成的方法
    @Autowired
    private StockService stockService;

    /**
     * 首页四个卡片的数据
     */
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> data = new HashMap<>();

        // 1、药品总数，直接count一下drug表就行
        Long totalDrugs = drugMapper.selectCount(null);
        data.put("totalDrugs", totalDrugs);

        // 2、库存总量，把所有机构的库存数量加起来
        // 这里是把记录全查出来用循环加的，图省事，数据多了应该用SQL的sum
        // TODO: 后面库存记录多了改成 select sum(quantity) from stock
        List<Stock> stockList = stockMapper.selectList(null);
        int totalInventory = 0;
        for (int i = 0; i < stockList.size(); i++) {
            Stock stock = stockList.get(i);
            if (stock.getQuantity() != null) {
                totalInventory = totalInventory + stock.getQuantity();
            }
        }
        data.put("totalInventory", totalInventory);

        // 3、今日订单，就是今天0点以后下的单
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<DrugOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.ge(DrugOrder::getCreateTime, todayStart);
        Long todayOrders = drugOrderMapper.selectCount(orderWrapper);
        data.put("todayOrders", todayOrders);

        // 4、待配送任务，就是配送任务里状态是0(待配送)的那些
        // 状态值是跟着 Constants 里定义的：0-待配送 1-配送中 2-已送达 3-已完成
        LambdaQueryWrapper<DeliveryTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(DeliveryTask::getStatus, com.medical.drugdelivery.common.Constants.DELIVERY_STATUS_PENDING);
        Long pendingTasks = deliveryTaskMapper.selectCount(taskWrapper);
        data.put("pendingTasks", pendingTasks);

        return data;
    }

    /**
     * 最近7天的订单趋势
     * 折线图的x轴是日期，y轴是订单数或者金额
     */
    @Override
    public Map<String, Object> getOrderTrend() {
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();         // 日期，格式 10-14 这种
        List<Integer> orderCounts = new ArrayList<>();  // 每天的订单数
        List<BigDecimal> orderAmounts = new ArrayList<>(); // 每天的订单金额

        // 先把最近7天的订单一次性查出来，再去循环统计
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(6); // 往前数6天，加上今天就正好7天
        LambdaQueryWrapper<DrugOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(DrugOrder::getCreateTime, startDay.atStartOfDay());
        List<DrugOrder> orderList = drugOrderMapper.selectList(wrapper);

        // 一天一天统计，用了两层循环
        // TODO: 数据多了这样一层层循环太慢，后面可以改成SQL的group by
        for (int i = 0; i < 7; i++) {
            LocalDate day = startDay.plusDays(i);
            // 格式化成 10-14 这种，前端直接拿来当x轴
            String dayStr = day.format(DateTimeFormatter.ofPattern("MM-dd"));
            dates.add(dayStr);

            int count = 0;
            BigDecimal amount = BigDecimal.ZERO;
            for (int j = 0; j < orderList.size(); j++) {
                DrugOrder order = orderList.get(j);
                // 下单时间不为空，而且是这一天的，就统计上
                if (order.getCreateTime() != null && order.getCreateTime().toLocalDate().equals(day)) {
                    count = count + 1;
                    if (order.getTotalAmount() != null) {
                        amount = amount.add(order.getTotalAmount());
                    }
                }
            }
            orderCounts.add(count);
            orderAmounts.add(amount);
        }

        data.put("dates", dates);
        data.put("orderCounts", orderCounts);
        data.put("orderAmounts", orderAmounts);
        return data;
    }

    /**
     * 药品分类占比，环形图用
     * 统计每个分类下面有多少种药品
     */
    @Override
    public List<Map<String, Object>> getCategoryRatio() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 分类和药品都查出来，然后在Java里对
        List<DrugCategory> categories = drugCategoryMapper.selectList(null);
        List<Drug> drugs = drugMapper.selectList(null);

        for (int i = 0; i < categories.size(); i++) {
            DrugCategory category = categories.get(i);
            int count = 0;
            for (int j = 0; j < drugs.size(); j++) {
                Drug drug = drugs.get(j);
                if (drug.getCategoryId() != null && drug.getCategoryId().equals(category.getId())) {
                    count = count + 1;
                }
            }
            // 没药品的分类就不显示了，不然饼图上全是0不好看
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", category.getCategoryName());
                item.put("value", count);
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 库存预警列表
     * 直接调库存模块现成的预警查询，省得再写一遍
     */
    @Override
    public List<Map<String, Object>> getStockWarning() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 不传机构就是查全部机构的
        List<Stock> list = stockService.getWarningList(null);
        for (int i = 0; i < list.size(); i++) {
            Stock stock = list.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("id", stock.getId());
            item.put("drugName", stock.getDrugName());
            item.put("stock", stock.getQuantity());
            item.put("minStock", stock.getWarningThreshold());
            item.put("unit", stock.getUnit());
            result.add(item);
        }
        return result;
    }

    /**
     * 最近的订单，取最近5条
     */
    @Override
    public List<Map<String, Object>> getRecentOrders() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 按创建时间倒序，只取5条
        LambdaQueryWrapper<DrugOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DrugOrder::getCreateTime);
        wrapper.last("limit 5");
        List<DrugOrder> orders = drugOrderMapper.selectList(wrapper);

        for (int i = 0; i < orders.size(); i++) {
            DrugOrder order = orders.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("orderNo", order.getOrderNo());
            item.put("institutionName", getInstitutionName(order.getInstitutionId()));
            // 金额有可能是空的，给个0比较安全，前端要 toFixed(2)
            if (order.getTotalAmount() == null) {
                item.put("totalAmount", BigDecimal.ZERO);
            } else {
                item.put("totalAmount", order.getTotalAmount());
            }
            // 前端是按字符串判断颜色的，所以这里给个英文的key
            item.put("status", getStatusKey(order.getStatus()));
            item.put("statusName", getStatusName(order.getStatus()));
            item.put("createTime", order.getCreateTime());
            result.add(item);
        }
        return result;
    }

    /**
     * 配送任务各状态的数量
     */
    @Override
    public Map<String, Object> getDeliveryStats() {
        Map<String, Object> data = new HashMap<>();

        // 状态值跟 Constants 一致：0-待配送 1-配送中 2-已送达 3-已完成
        LambdaQueryWrapper<DeliveryTask> w1 = new LambdaQueryWrapper<>();
        w1.eq(DeliveryTask::getStatus, 0);
        data.put("pending", deliveryTaskMapper.selectCount(w1));

        LambdaQueryWrapper<DeliveryTask> w2 = new LambdaQueryWrapper<>();
        w2.eq(DeliveryTask::getStatus, 1);
        data.put("delivering", deliveryTaskMapper.selectCount(w2));

        LambdaQueryWrapper<DeliveryTask> w3 = new LambdaQueryWrapper<>();
        w3.eq(DeliveryTask::getStatus, 2);
        data.put("delivered", deliveryTaskMapper.selectCount(w3));

        LambdaQueryWrapper<DeliveryTask> w4 = new LambdaQueryWrapper<>();
        w4.eq(DeliveryTask::getStatus, 3);
        data.put("completed", deliveryTaskMapper.selectCount(w4));

        return data;
    }

    /**
     * 根据机构id查机构名字
     * 查不到就返回个空字符串，免得前端显示null
     */
    private String getInstitutionName(Long institutionId) {
        if (institutionId == null) {
            return "";
        }
        Institution institution = institutionMapper.selectById(institutionId);
        if (institution == null || institution.getName() == null) {
            return "";
        }
        return institution.getName();
    }

    /**
     * 订单状态数字转成英文key
     * 前端的标签颜色是按这个key来的，所以得对应上
     * 注意状态值要跟 Constants 里定义的一致：
     * 0-待审核 1-已审核 2-已驳回 3-配送中 4-已完成 5-已取消
     */
    private String getStatusKey(Integer status) {
        if (status == null) {
            return "";
        }
        if (status == 0) {
            return "pending";
        } else if (status == 1) {
            return "approved";
        } else if (status == 2) {
            return "rejected";
        } else if (status == 3) {
            return "delivering";
        } else if (status == 4) {
            return "completed";
        } else if (status == 5) {
            return "cancelled";
        }
        return "";
    }

    /**
     * 订单状态转成中文，给前端显示用
     * 0-待审核 1-已审核 2-已驳回 3-配送中 4-已完成 5-已取消
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        if (status == 0) {
            return "待审核";
        } else if (status == 1) {
            return "已审核";
        } else if (status == 2) {
            return "已驳回";
        } else if (status == 3) {
            return "配送中";
        } else if (status == 4) {
            return "已完成";
        } else if (status == 5) {
            return "已取消";
        }
        return "未知";
    }
}
