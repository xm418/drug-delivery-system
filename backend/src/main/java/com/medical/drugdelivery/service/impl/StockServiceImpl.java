package com.medical.drugdelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.drugdelivery.common.Constants;
import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.ResultCode;
import com.medical.drugdelivery.config.BusinessException;
import com.medical.drugdelivery.dto.StockOperationDTO;
import com.medical.drugdelivery.dto.StockQueryDTO;
import com.medical.drugdelivery.entity.Drug;
import com.medical.drugdelivery.entity.Institution;
import com.medical.drugdelivery.entity.Stock;
import com.medical.drugdelivery.entity.StockRecord;
import com.medical.drugdelivery.mapper.DrugMapper;
import com.medical.drugdelivery.mapper.InstitutionMapper;
import com.medical.drugdelivery.mapper.StockMapper;
import com.medical.drugdelivery.mapper.StockRecordMapper;
import com.medical.drugdelivery.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================
// 【CRUD Service 实现】StockServiceImpl
// 作用：业务逻辑层，调用 Mapper（继承 BaseMapper）执行数据库操作
//   add()    → 新增：补默认值 → mapper.insert()
//   page()   → 分页查询：构建 LambdaQueryWrapper 条件 → mapper.selectPage()
//   getById()→ 按 id 查详情：mapper.selectById() → 补关联表名称
//   update() → 修改：mapper.updateById()（主键 id 必填）
//   delete() → 删除：mapper.deleteById()（因 @TableLogic 实际是 UPDATE SET deleted=1）
// 事务：@Transactional 保证双表写入（如订单主表+明细）原子性
// ============================================================


/**
 * 库存服务实现类
 * 主要负责药品库存的增删改查、入库出库调拨等操作
 *
 * @author 学生
 * @version 1.0
 * @date 2024-xx-xx
 */
@Service
public class StockServiceImpl implements StockService {

    // TODO: 老师说字段注入不推荐，以后有空改成构造器注入，先这样用吧
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    /**
     * 分页查询库存列表
     * 支持按药品ID、机构ID查询，还可以只看预警的库存
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果
     */
    @Override
    public PageResult<Stock> page(StockQueryDTO queryDTO) {
        // 创建分页对象，传入页码和每页大小
        Page<Stock> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 构建查询条件包装器
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();

        // 如果药品ID不为空，就按药品ID查询
        if (queryDTO.getDrugId() != null) {
            wrapper.eq(Stock::getDrugId, queryDTO.getDrugId());
        }
        // 如果机构ID不为空，就按机构ID查询
        if (queryDTO.getInstitutionId() != null) {
            wrapper.eq(Stock::getInstitutionId, queryDTO.getInstitutionId());
        }
        // 如果只看预警的，就加个条件：数量小于等于预警阈值
        // TODO: 这里用了原生sql，会不会有sql注入的风险？老师说尽量不用apply
        if (Boolean.TRUE.equals(queryDTO.getWarningOnly())) {
            wrapper.and(w -> w.apply("quantity <= (select warning_threshold from drug where id = stock.drug_id)"));
        }
        // 按更新时间倒序排列
        wrapper.orderByDesc(Stock::getUpdateTime);

        // 执行分页查询
        Page<Stock> result = stockMapper.selectPage(page, wrapper);
        // 遍历每条记录，填充药品名称和机构名称等信息
        // 原来用的forEach，老师说改成普通for循环更容易理解
        List<Stock> records = result.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Stock stock = records.get(i);
            fillStockInfo(stock);
        }

        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 根据ID查询库存信息
     *
     * @param id 库存ID
     * @return 库存对象
     */
    @Override
    public Stock getById(Long id) {
        // 根据id查库存
        Stock stock = stockMapper.selectById(id);
        // 如果查到了，就填充一下关联信息
        if (stock != null) {
            fillStockInfo(stock);
        }
        return stock;
    }

    /**
     * 根据药品ID和机构ID查询库存
     * 这个方法挺常用的，入库出库都要用到
     *
     * @param drugId        药品ID
     * @param institutionId 机构ID
     * @return 库存对象
     */
    @Override
    public Stock getByDrugAndInstitution(Long drugId, Long institutionId) {
        // 构建查询条件
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getDrugId, drugId);
        wrapper.eq(Stock::getInstitutionId, institutionId);
        // 查询一条记录
        Stock stock = stockMapper.selectOne(wrapper);
        // 不为空的话填充信息
        if (stock != null) {
            fillStockInfo(stock);
        }
        return stock;
    }

    /**
     * 库存入库操作
     * 增加库存数量，同时记录入库流水
     *
     * @param operationDTO 操作DTO，包含药品ID、机构ID、数量等信息
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】stockIn()
    public void stockIn(StockOperationDTO operationDTO, Long operatorId, String operatorName) {
        // 先获取或者创建库存记录（如果不存在就新建一个）
        Stock stock = getOrCreateStock(operationDTO.getDrugId(), operationDTO.getInstitutionId());

        // 记录入库前的数量
        int beforeQty = stock.getQuantity();
        // 加上入库的数量
        stock.setQuantity(beforeQty + operationDTO.getQuantity());

        // 下面这些字段如果传了就更新，没传就不更新
        // TODO: 这里是不是可以用BeanUtils.copyProperties？但是老师说要手动写清楚
        if (operationDTO.getUnitPrice() != null) {
            stock.setUnitPrice(operationDTO.getUnitPrice());
        }
        // 批号不为空就更新
        if (operationDTO.getBatchNumber() != null && !operationDTO.getBatchNumber().equals("")) {
            stock.setBatchNumber(operationDTO.getBatchNumber());
        }
        // 生产日期不为空就更新
        if (operationDTO.getProductionDate() != null) {
            stock.setProductionDate(operationDTO.getProductionDate());
        }
        // 有效期不为空就更新
        if (operationDTO.getExpiryDate() != null) {
            stock.setExpiryDate(operationDTO.getExpiryDate());
        }
        // 更新库存
        stockMapper.updateById(stock);

        // 保存库存操作记录
        saveRecord(operationDTO.getDrugId(), operationDTO.getInstitutionId(),
                Constants.STOCK_OP_IN, operationDTO.getQuantity(),
                beforeQty, stock.getQuantity(),
                operationDTO.getBatchNumber(), operatorId, operatorName,
                operationDTO.getRemark(), null);
    }

    /**
     * 库存出库操作
     * 减少库存数量，同时记录出库流水
     * 如果库存不足会抛出异常
     *
     * @param operationDTO 操作DTO
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】stockOut()
    public void stockOut(StockOperationDTO operationDTO, Long operatorId, String operatorName) {
        // 先查一下有没有这个库存
        Stock stock = getByDrugAndInstitution(operationDTO.getDrugId(), operationDTO.getInstitutionId());
        // 库存不存在或者数量不够，就抛异常
        if (stock == null || stock.getQuantity() < operationDTO.getQuantity()) {
            throw new BusinessException(ResultCode.INSUFFICIENT_STOCK);
        }

        // 记录出库前数量
        int beforeQty = stock.getQuantity();
        // 减去出库数量
        stock.setQuantity(beforeQty - operationDTO.getQuantity());
        // 更新库存
        stockMapper.updateById(stock);

        // 保存出库记录
        saveRecord(operationDTO.getDrugId(), operationDTO.getInstitutionId(),
                Constants.STOCK_OP_OUT, operationDTO.getQuantity(),
                beforeQty, stock.getQuantity(),
                stock.getBatchNumber(), operatorId, operatorName,
                operationDTO.getRemark(), null);
    }

    /**
     * 库存调拨操作
     * 从一个机构调到另一个机构
     * 两边都要更新库存，还要各记一条流水
     *
     * @param operationDTO 操作DTO，包含调出机构和调入机构
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    // 【CRUD/业务】stockTransfer()
    public void stockTransfer(StockOperationDTO operationDTO, Long operatorId, String operatorName) {
        // 调出机构ID
        Long fromId = operationDTO.getFromInstitutionId();
        // 调入机构ID
        Long toId = operationDTO.getToInstitutionId();

        // 先查调出方的库存够不够
        Stock fromStock = getByDrugAndInstitution(operationDTO.getDrugId(), fromId);
        if (fromStock == null || fromStock.getQuantity() < operationDTO.getQuantity()) {
            throw new BusinessException(ResultCode.INSUFFICIENT_STOCK);
        }

        // 调出方减库存
        int fromBefore = fromStock.getQuantity();
        fromStock.setQuantity(fromBefore - operationDTO.getQuantity());
        stockMapper.updateById(fromStock);

        // 调入方加库存（没有就新建）
        Stock toStock = getOrCreateStock(operationDTO.getDrugId(), toId);
        int toBefore = toStock.getQuantity();
        toStock.setQuantity(toBefore + operationDTO.getQuantity());
        stockMapper.updateById(toStock);

        // 调出方记一条流水
        saveRecord(operationDTO.getDrugId(), fromId,
                Constants.STOCK_OP_TRANSFER, operationDTO.getQuantity(),
                fromBefore, fromStock.getQuantity(),
                fromStock.getBatchNumber(), operatorId, operatorName,
                "调拨出库", null);

        // 调入方记一条流水
        // TODO: 这里两条saveRecord能不能合并成一个方法调用？看起来重复代码有点多
        saveRecord(operationDTO.getDrugId(), toId,
                Constants.STOCK_OP_TRANSFER, operationDTO.getQuantity(),
                toBefore, toStock.getQuantity(),
                fromStock.getBatchNumber(), operatorId, operatorName,
                "调拨入库", null);
    }

    /**
     * 获取库存预警列表
     * 查询所有数量低于预警阈值的库存记录
     *
     * @param institutionId 机构ID，可以为空，为空则查所有机构
     * @return 预警库存列表
     */
    @Override
    public List<Stock> getWarningList(Long institutionId) {
        // 构建查询条件
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        // 如果指定了机构就按机构查
        if (institutionId != null) {
            wrapper.eq(Stock::getInstitutionId, institutionId);
        }
        // 数量小于等于预警阈值的才查出来
        wrapper.and(w -> w.apply("quantity <= (select warning_threshold from drug where id = stock.drug_id)"));
        // 按数量升序排，最少的排最前面
        wrapper.orderByAsc(Stock::getQuantity);
        // 执行查询
        List<Stock> list = stockMapper.selectList(wrapper);
        // 遍历填充信息，用普通for循环老师说更清楚
        for (int i = 0; i < list.size(); i++) {
            Stock stock = list.get(i);
            fillStockInfo(stock);
        }
        return list;
    }

    /**
     * 分页查询库存操作记录
     * 也就是出入库调拨的流水记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<StockRecord> recordPage(StockQueryDTO queryDTO) {
        // 分页对象
        Page<StockRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询条件
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();

        // 药品ID不为空就加上
        if (queryDTO.getDrugId() != null) {
            wrapper.eq(StockRecord::getDrugId, queryDTO.getDrugId());
        }
        // 机构ID不为空就加上
        if (queryDTO.getInstitutionId() != null) {
            wrapper.eq(StockRecord::getInstitutionId, queryDTO.getInstitutionId());
        }
        // 按创建时间倒序，最新的在前面
        wrapper.orderByDesc(StockRecord::getCreateTime);

        // 执行分页查询
        Page<StockRecord> result = stockRecordMapper.selectPage(page, wrapper);
        // 遍历填充药品名和机构名
        // 用普通for循环，不用forEach了
        List<StockRecord> records = result.getRecords();
        for (int i = 0; i < records.size(); i++) {
            StockRecord record = records.get(i);
            fillRecordInfo(record);
        }

        return PageResult.of(result.getTotal(), result.getRecords(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    /**
     * 获取或者创建库存记录
     * 如果指定药品和机构的库存不存在，就新建一个数量为0的
     *
     * @param drugId        药品ID
     * @param institutionId 机构ID
     * @return 库存对象
     */
    private Stock getOrCreateStock(Long drugId, Long institutionId) {
        // 先查一下有没有
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Stock::getDrugId, drugId);
        wrapper.eq(Stock::getInstitutionId, institutionId);
        Stock stock = stockMapper.selectOne(wrapper);

        // 没有的话就新建一个
        if (stock == null) {
            stock = new Stock();
            stock.setDrugId(drugId);
            stock.setInstitutionId(institutionId);
            stock.setQuantity(0); // 初始数量0
            stockMapper.insert(stock);
        }
        return stock;
    }

    /**
     * 保存库存操作记录
     * 每次入库出库调拨都要调用这个方法记一条流水
     *
     * @param drugId          药品ID
     * @param institutionId   机构ID
     * @param opType          操作类型（入库/出库/调拨）
     * @param quantity        操作数量
     * @param beforeQty       操作前数量
     * @param afterQty        操作后数量
     * @param batchNumber     批号
     * @param operatorId      操作人ID
     * @param operatorName    操作人姓名
     * @param remark          备注
     * @param relatedOrderNo  关联订单号
     */
    private void saveRecord(Long drugId, Long institutionId, Integer opType, Integer quantity,
                            Integer beforeQty, Integer afterQty, String batchNumber,
                            Long operatorId, String operatorName, String remark, String relatedOrderNo) {
        // 新建一条记录对象
        StockRecord record = new StockRecord();
        record.setDrugId(drugId);
        record.setInstitutionId(institutionId);
        record.setOperationType(opType);
        record.setQuantity(quantity);
        record.setBeforeQuantity(beforeQty);
        record.setAfterQuantity(afterQty);
        record.setBatchNumber(batchNumber);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRemark(remark);
        record.setRelatedOrderNo(relatedOrderNo);
        // 插入数据库
        stockRecordMapper.insert(record);
    }

    /**
     * 填充库存的附加信息
     * 比如药品名称、药品编码、规格、单位、预警阈值，还有机构名称
     *
     * @param stock 库存对象
     */
    private void fillStockInfo(Stock stock) {
        // 填充药品相关信息
        if (stock.getDrugId() != null) {
            Drug drug = drugMapper.selectById(stock.getDrugId());
            if (drug != null) {
                stock.setDrugName(drug.getDrugName());
                stock.setDrugCode(drug.getDrugCode());
                stock.setSpecification(drug.getSpecification());
                stock.setUnit(drug.getUnit());
                stock.setWarningThreshold(drug.getWarningThreshold());
            }
        }
        // 填充机构名称
        if (stock.getInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(stock.getInstitutionId());
            if (inst != null) {
                // 机构名称不为空才设置，防止空指针
                if (inst.getName() != null && !inst.getName().equals("")) {
                    stock.setInstitutionName(inst.getName());
                }
            }
        }
    }

    /**
     * 填充库存记录的附加信息
     * 主要是药品名称和机构名称，方便前端展示
     *
     * @param record 库存记录对象
     */
    private void fillRecordInfo(StockRecord record) {
        // 填充药品名称
        if (record.getDrugId() != null) {
            Drug drug = drugMapper.selectById(record.getDrugId());
            if (drug != null) {
                record.setDrugName(drug.getDrugName());
            }
        }
        // 填充机构名称
        if (record.getInstitutionId() != null) {
            Institution inst = institutionMapper.selectById(record.getInstitutionId());
            if (inst != null) {
                // 用最朴素的方式判断字符串非空
                if (inst.getName() != null && !inst.getName().equals("")) {
                    record.setInstitutionName(inst.getName());
                }
            }
        }
    }
}
