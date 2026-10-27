package com.medical.drugdelivery.controller;

import com.medical.drugdelivery.common.PageResult;
import com.medical.drugdelivery.common.Result;
import com.medical.drugdelivery.dto.StockOperationDTO;
import com.medical.drugdelivery.dto.StockQueryDTO;
import com.medical.drugdelivery.entity.Stock;
import com.medical.drugdelivery.entity.StockRecord;
import com.medical.drugdelivery.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================
// 【CRUD Controller】StockController
// 作用：接收前端 HTTP 请求，调用 Service 执行业务逻辑
// 接口路径前缀：/api/stock
//   POST    → 新增（Create）
//   GET     → 查询（Read，含分页列表 / 按 id 详情）
//   PUT     → 修改（Update）
//   DELETE  → 删除（Delete，逻辑删 @TableLogic）
// ============================================================


/**
 * 库存管理Controller
 * 库存查询、入库、出库、调拨、预警、出入库记录
 */
@RestController
// 【CRUD】请求 - ()：请求Stock
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * 分页查询库存列表
     * 可以按机构、药品、库存数量范围筛选
     */
    // 【CRUD】查询 - page()：查询Stock
    @GetMapping("/page")
    public Result<PageResult<Stock>> page(StockQueryDTO queryDTO) {
        PageResult<Stock> page = stockService.page(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据id查询库存
     */
    // 【CRUD】查询 - getById()：查询Stock
    @GetMapping("/{id}")
    public Result<Stock> getById(@PathVariable Long id) {
        Stock stock = stockService.getById(id);
        return Result.success(stock);
    }

    /**
     * 根据药品和机构查询库存
     * 下单的时候检查库存够不够用
     */
    // 【CRUD】查询 - getByDrugAndInstitution()：查询Stock
    @GetMapping("/drug/{drugId}/institution/{institutionId}")
    public Result<Stock> getByDrugAndInstitution(@PathVariable Long drugId, @PathVariable Long institutionId) {
        Stock stock = stockService.getByDrugAndInstitution(drugId, institutionId);
        return Result.success(stock);
    }

    /**
     * 入库操作
     * 增加库存，同时记录一条入库流水
     */
    // 【CRUD】新增 - stockIn()：新增Stock
    @PostMapping("/in")
    public Result<Void> stockIn(@Valid @RequestBody StockOperationDTO operationDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        stockService.stockIn(operationDTO, userId, username);
        return Result.success();
    }

    /**
     * 出库操作
     * 减少库存，同时记录一条出库流水
     * 注意：库存不够的话会报错
     */
    // 【CRUD】新增 - stockOut()：新增Stock
    @PostMapping("/out")
    public Result<Void> stockOut(@Valid @RequestBody StockOperationDTO operationDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        stockService.stockOut(operationDTO, userId, username);
        return Result.success();
    }

    /**
     * 库存调拨
     * 从一个机构调到另一个机构
     * 其实就是一边出库一边入库，要加事务保证两边都成功或者都失败
     */
    // 【CRUD】新增 - stockTransfer()：新增Stock
    @PostMapping("/transfer")
    public Result<Void> stockTransfer(@Valid @RequestBody StockOperationDTO operationDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        stockService.stockTransfer(operationDTO, userId, username);
        return Result.success();
    }

    /**
     * 获取库存预警列表
     * 库存低于预警阈值的药品都列出来
     * 首页大屏会用到
     */
    // 【CRUD】查询 - getWarningList()：查询Stock
    @GetMapping("/warning")
    public Result<List<Stock>> getWarningList(@RequestParam(required = false) Long institutionId) {
        List<Stock> list = stockService.getWarningList(institutionId);
        return Result.success(list);
    }

    /**
     * 分页查询出入库记录
     * 库存流水账，查历史用的
     */
    // 【CRUD】查询 - recordPage()：查询Stock
    @GetMapping("/records")
    public Result<PageResult<StockRecord>> recordPage(StockQueryDTO queryDTO) {
        PageResult<StockRecord> page = stockService.recordPage(queryDTO);
        return Result.success(page);
    }
}
