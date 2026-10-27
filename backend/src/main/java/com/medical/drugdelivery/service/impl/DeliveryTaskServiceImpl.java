package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.config.BusinessException;
import com.medical.drugdelivery.dto.DeliveryQueryDTO;
import com.medical.drugdelivery.entity.DeliveryTask;
import com.medical.drugdelivery.entity.DrugOrder;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.mapper.DeliveryTaskMapper;
import com.medical.drugdelivery.mapper.DrugOrderMapper;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.service.DeliveryTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ============================================================
// 【CRUD Service 实现】DeliveryTaskServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 配送任务服务实现类
 * 主要处理药品配送任务的增删改查
 * TODO: 后续可以加缓存优化一下，查的有点频繁
 * 学生：XXX 日期：2024年X月X日
 */
@Service
public class DeliveryTaskServiceImpl implements DeliveryTaskService {

    // 配送任务Mapper，用Autowired注入的哦
    @Autowired
    private DeliveryTaskMapper deliveryTaskMapper;

    // 药品订单Mapper
    @Autowired
    private DrugOrderMapper orderMapper;

    // 机构Mapper
    @Autowired
    private InstitutionMapper institutionMapper;

    /**
     * 分页查询配送任务列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<DeliveryTask> page(DeliveryQueryDTO queryDTO) {
        // 创建分页对象，传页码和每页大小
        Page<DeliveryTask> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 构造查询条件
        LambdaQueryWrapper<DeliveryTask> wrapper = new LambdaQueryWrapper<>();

        // 如果传了状态，就按状态查
        if (queryDTO.getStatus() != null) {
            wrapper.eq(DeliveryTask::getStatus, queryDTO.getStatus());
        }
        // 如果传了订单ID，就按订单ID查
        if (queryDTO.getOrderId() != null) {
            wrapper.eq(DeliveryTask::getOrderId, queryDTO.getOrderId());
        }
        // 如果传了发货机构ID，就按发货机构查
        if (queryDTO.getFromInstitutionId() != null) {
            wrapper.eq(DeliveryTask::getFromInstitutionId, queryDTO.getFromInstitutionId());
        }
        // 如果传了收货机构ID，就按收货机构查
        if (queryDTO.getToInstitutionId() != null) {
            wrapper.eq(DeliveryTask::getToInstitutionId, queryDTO.getToInstitutionId());
        }
        // 按创建时间倒序排列，最新的在前面
        wrapper.orderByDesc(DeliveryTask::getCreateTime);

        // 执行分页查询
        Page<DeliveryTask> result = deliveryTaskMapper.selectPage(page, wrapper);

        // 遍历每条记录，补充订单号和机构名称等信息
        // 这里本来想写forEach的，但是老师说要用普通for循环
        List<DeliveryTask> records = result.getRecords();
        for (int i = 0; i < records.size(); i++) {
            DeliveryTask task = records.get(i);
            fillTaskInfo(task);
        }

        // 封装返回结果
        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据ID查询配送任务详情
     * @param id 任务ID
     * @return 配送任务对象
     */
    @Override
    public DeliveryTask getById(Long id) {
        // 根据ID查任务
        DeliveryTask task = deliveryTaskMapper.selectById(id);
        // 如果查到了，就补充一下关联信息
        if (task != null) {
            fillTaskInfo(task);
        }
        return task;
    }

    /**
     * 创建配送任务
     * 注意：创建后会把订单状态改为配送中
     * TODO: 这里是不是要加个库存扣减的逻辑？老师说后面再说
     * @param orderId 订单ID
     * @param fromInstitutionId 发货机构ID
     * @param toInstitutionId 收货机构ID
     * @param deliveryPersonName 配送人姓名
     * @param deliveryPhone 配送人电话
     * @param remark 备注
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】create()
    public void create(Long orderId, Long fromInstitutionId, Long toInstitutionId,
                       String deliveryPersonName, String deliveryPhone, String remark) {
        // 先查订单存不存在
        DrugOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 订单状态必须是已审核才能创建配送任务
        if (!Constants.ORDER_STATUS_APPROVED.equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确，无法创建配送任务");
        }

        // 新建一个配送任务对象
        DeliveryTask task = new DeliveryTask();
        // 生成任务编号，PS开头的
        task.setTaskNo(generateTaskNo());
        task.setOrderId(orderId);
        task.setFromInstitutionId(fromInstitutionId);
        task.setToInstitutionId(toInstitutionId);
        task.setDeliveryPersonName(deliveryPersonName);
        task.setDeliveryPhone(deliveryPhone);
        // 初始状态是待配送
        task.setStatus(Constants.DELIVERY_STATUS_PENDING);
        task.setRemark(remark);
        // 插入数据库
        deliveryTaskMapper.insert(task);

        // 把订单状态改成配送中
        order.setStatus(Constants.ORDER_STATUS_DELIVERING);
        orderMapper.updateById(order);
    }

    /**
     * 更新配送任务状态
     * TODO: 状态流转要不要做校验？目前先这样吧
     * @param id 任务ID
     * @param status 新状态
     * @param remark 备注（可选）
     */
    @Override
    // 【CRUD/业务】updateStatus()
    public void updateStatus(Long id, Integer status, String remark) {
        // 先查任务存不存在
        DeliveryTask task = deliveryTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("配送任务不存在");
        }
        // 设置新状态
        task.setStatus(status);
        // 如果有备注就更新备注
        if (remark != null) {
            task.setRemark(remark);
        }
        // 更新到数据库
        deliveryTaskMapper.updateById(task);
    }

    /**
     * 开始配送
     * 把任务状态从待配送改为配送中，并记录开始配送时间
     * @param id 任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】startDelivery()
    public void startDelivery(Long id) {
        // 查任务
        DeliveryTask task = deliveryTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("配送任务不存在");
        }
        // 只有待配送状态的任务才能开始配送
        if (!Constants.DELIVERY_STATUS_PENDING.equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确");
        }
        // 改成配送中
        task.setStatus(Constants.DELIVERY_STATUS_DELIVERING);
        // 记录配送开始时间
        task.setDeliveryTime(LocalDateTime.now());
        deliveryTaskMapper.updateById(task);
    }

    /**
     * 完成配送
     * 把任务状态从配送中改为已完成，同时把关联订单也改成已完成
     * TODO: 完成配送的时候要不要让用户签收确认一下？
     * @param id 任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】completeDelivery()
    public void completeDelivery(Long id) {
        // 查任务
        DeliveryTask task = deliveryTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("配送任务不存在");
        }
        // 只有配送中状态的任务才能完成
        if (!Constants.DELIVERY_STATUS_DELIVERING.equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确");
        }
        // 改成已完成
        task.setStatus(Constants.DELIVERY_STATUS_COMPLETED);
        // 记录收货时间
        task.setReceiveTime(LocalDateTime.now());
        deliveryTaskMapper.updateById(task);

        // 顺便把订单也改成已完成状态
        DrugOrder order = orderMapper.selectById(task.getOrderId());
        if (order != null) {
            order.setStatus(Constants.ORDER_STATUS_COMPLETED);
            orderMapper.updateById(order);
        }
    }

    /**
     * 生成配送任务编号
     * 格式：PS + 年月日时分秒 + 4位随机数
     * TODO: 用随机数会不会重复啊？老师说概率很小先这样
     * @return 任务编号
     */
    private String generateTaskNo() {
        // 获取当前时间，格式化成yyyyMMddHHmmss
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 生成一个0-9999的随机数
        int random = (int) (Math.random() * 10000);
        // 拼接起来，随机数不够4位前面补0
        return "PS" + dateStr + String.format("%04d", random);
    }

    /**
     * 填充任务的附加信息
     * 包括订单号、发货机构名称、收货机构名称
     * @param task 配送任务对象
     */
    private void fillTaskInfo(DeliveryTask task) {
        // 如果有订单ID，就把订单号查出来填上
        if (task.getOrderId() != null) {
            DrugOrder order = orderMapper.selectById(task.getOrderId());
            if (order != null) {
                task.setOrderNo(order.getOrderNo());
            }
        }
        // 如果有发货机构ID，就把机构名称查出来填上
        if (task.getFromInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(task.getFromInstitutionId());
            if (inst != null) {
                task.setFromInstitutionName(inst.getName());
            }
        }
        // 如果有收货机构ID，就把机构名称查出来填上
        if (task.getToInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(task.getToInstitutionId());
            if (inst != null) {
                task.setToInstitutionName(inst.getName());
            }
        }
    }
}
