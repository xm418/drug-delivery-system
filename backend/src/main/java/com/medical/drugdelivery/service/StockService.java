package com.medical.drugdelivery.service;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.dto.StockOperationDTO;
import com.medical.drugdelivery.dto.StockQueryDTO;
import com.medical.drugdelivery.entity.Stock;
import com.medical.drugdelivery.entity.StockRecord;

import java.util.List;

/**
 * 库存Service接口
 * 管各机构的药品库存，入库、出库、调拨都会更新库存并记流水
 */
public interface StockService {

    /** 分页查询库存列表 */
    PageResult<Stock> page(StockQueryDTO queryDTO);

    /** 根据id查库存 */
    Stock getById(Long id);

    /** 按药品+机构查库存，一个机构下一种药只有一条库存记录 */
    Stock getByDrugAndInstitution(Long drugId, Long institutionId);

    /** 入库，库存加上，记一条入库流水 */
    void stockIn(StockOperationDTO operationDTO, Long operatorId, String operatorName);

    /** 出库，库存减掉（不够会报库存不足），记一条出库流水 */
    void stockOut(StockOperationDTO operationDTO, Long operatorId, String operatorName);

    /** 调拨，调出机构减库存、调入机构加库存，记两条流水 */
    void stockTransfer(StockOperationDTO operationDTO, Long operatorId, String operatorName);

    /** 查库存预警列表，就是库存量低于阈值的那些 */
    List<Stock> getWarningList(Long institutionId);

    /** 分页查库存流水记录 */
    PageResult<StockRecord> recordPage(StockQueryDTO queryDTO);
}
