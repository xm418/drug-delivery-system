package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.ResultCode;
import com.medical.drugdelivery.config.BusinessException;
import com.medical.drugdelivery.dto.OrderCreateDTO;
import com.medical.drugdelivery.dto.OrderItemDTO;
import com.medical.drugdelivery.dto.OrderQueryDTO;
import com.medical.drugdelivery.entity.Drug;
import com.medical.drugdelivery.entity.DrugOrder;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.entity.OrderItem;
import com.medical.drugdelivery.entity.SysUser;
import com.medical.drugdelivery.mapper.DrugMapper;
import com.medical.drugdelivery.mapper.DrugOrderMapper;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.mapper.OrderItemMapper;
import com.medical.drugdelivery.mapper.SysUserMapper;
import com.medical.drugdelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// 【CRUD Service 实现】OrderServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 订单Service实现类
 * 订单的创建、审核、取消、查询
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DrugOrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 分页查询订单列表
     */
    @Override
    public PageResult<DrugOrder> page(OrderQueryDTO queryDTO) {
        Page<DrugOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DrugOrder> wrapper = new LambdaQueryWrapper<>();

        // 按订单号模糊搜索
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().equals("")) {
            wrapper.like(DrugOrder::getOrderNo, queryDTO.getOrderNo());
        }
        // 按状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(DrugOrder::getStatus, queryDTO.getStatus());
        }
        // 按下单机构筛选
        if (queryDTO.getInstitutionId() != null) {
            wrapper.eq(DrugOrder::getInstitutionId, queryDTO.getInstitutionId());
        }
        // 按创建时间倒序，最新的在前面
        wrapper.orderByDesc(DrugOrder::getCreateTime);

        Page<DrugOrder> result = orderMapper.selectPage(page, wrapper);

        // 填充机构名称、下单人姓名这些
        for (DrugOrder order : result.getRecords()) {
            fillOrderInfo(order);
        }

        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据id查询订单基本信息
     */
    @Override
    public DrugOrder getById(Long id) {
        DrugOrder order = orderMapper.selectById(id);
        if (order != null) {
            fillOrderInfo(order);
        }
        return order;
    }

    /**
     * 查询订单详情（包含订单项明细）
     * 订单详情页用的
     */
    @Override
    public DrugOrder getDetail(Long id) {
        DrugOrder order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        fillOrderInfo(order);

        // 查询订单里的所有药品明细
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        order.setItems(items);

        return order;
    }

    /**
     * 创建订单
     * 要加事务，因为要同时插订单主表和订单明细表，一个失败就全回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】create()
    public void create(OrderCreateDTO createDTO, Long userId) {
        // 先创建订单主表记录
        DrugOrder order = new DrugOrder();
        order.setOrderNo(generateOrderNo()); // 生成订单号
        order.setInstitutionId(createDTO.getInstitutionId());
        order.setUserId(userId);
        order.setStatus(Constants.ORDER_STATUS_PENDING); // 待审核
        order.setRemark(createDTO.getRemark());

        BigDecimal totalAmount = BigDecimal.ZERO; // 总金额
        int totalQty = 0; // 总数量
        List<OrderItem> items = new ArrayList<>();

        // 遍历每个订单项，计算价格
        for (OrderItemDTO itemDTO : createDTO.getItems()) {
            // 先查药品信息，拿价格
            Drug drug = drugMapper.selectById(itemDTO.getDrugId());
            if (drug == null) {
                throw new BusinessException(ResultCode.DRUG_NOT_FOUND);
            }

            // 组装订单项
            OrderItem item = new OrderItem();
            item.setDrugId(itemDTO.getDrugId());
            item.setDrugName(drug.getDrugName());
            item.setSpecification(drug.getSpecification());
            item.setUnit(drug.getUnit());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(drug.getPrice());
            // 小计 = 单价 * 数量
            item.setTotalPrice(drug.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            items.add(item);

            // 累加总金额和总数量
            totalAmount = totalAmount.add(item.getTotalPrice());
            totalQty = totalQty + itemDTO.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQty);
        orderMapper.insert(order);

        // 插入订单明细，需要用到刚生成的订单id
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // TODO: 库存预占功能还没做，后面加
        // 现在下单不扣库存，审核通过生成配送任务的时候再扣
    }

    /**
     * 审核订单
     * approved=1 通过，approved=0 驳回
     * 只有待审核状态的订单才能审核
     * 加事务，因为后面要生成配送任务、扣库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】approve()
    public void approve(Long id, Integer approved, String remark, Long operatorId) {
        DrugOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 只有待审核的订单才能审核
        if (!Constants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        // 更新状态
        if (approved == 1) {
            order.setStatus(Constants.ORDER_STATUS_APPROVED); // 已审核
        } else {
            order.setStatus(Constants.ORDER_STATUS_REJECTED); // 已驳回
        }
        order.setApproveBy(operatorId);
        order.setApproveTime(LocalDateTime.now());
        order.setApproveRemark(remark);
        orderMapper.updateById(order);

        // TODO: 审核通过后自动生成配送任务
        // 这里要判断从哪个机构发货，可能还涉及到上级库存够不够的问题
        // 暂时先留着，后面和配送模块一起做
    }

    /**
     * 取消订单
     * 只有待审核的订单才能取消
     */
    @Override
    // 【CRUD/业务】cancel()
    public void cancel(Long id, Long userId) {
        DrugOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!Constants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        orderMapper.updateById(order);
    }

    /**
     * 生成订单号
     * 格式：DD + 时间戳(yyyyMMddHHmmss) + 4位随机数
     * 比如：DD202403081230451234
     * 这样基本不会重复，真重复了数据库唯一索引会报错，以后再优化
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "DD" + dateStr + String.format("%04d", random);
    }

    /**
     * 给订单填充附加信息（机构名称、下单人、审核人姓名）
     * 订单表里只有id，列表显示的时候需要名字
     */
    private void fillOrderInfo(DrugOrder order) {
        // 机构名称
        if (order.getInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(order.getInstitutionId());
            if (inst != null) {
                order.setInstitutionName(inst.getName());
            }
        }
        // 下单人姓名
        if (order.getUserId() != null) {
            SysUser user = userMapper.selectById(order.getUserId());
            if (user != null) {
                order.setUsername(user.getRealName());
            }
        }
        // 审核人姓名
        if (order.getApproveBy() != null) {
            SysUser user = userMapper.selectById(order.getApproveBy());
            if (user != null) {
                order.setApproveByName(user.getRealName());
            }
        }
    }
}
