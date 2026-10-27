import request from '@/utils/request'

// 库存相关的接口
// 最早这个文件是用 setTimeout + 写死的 mock 数组写的假数据，
// 后来 views/inventory/index.vue 改成调真接口了（它 import 的是 getInventoryList、stockIn、stockOut、stockTransfer、getWarningList），
// 之前名字对不上页面一打开就报错，所以现在全部改成真调后端。
// 后端接口都在 StockController 里，路径前缀是 /stocks；
// request 里 baseURL 已经配了 /api，所以下面 url 不用再写 /api。
// 响应拦截器会把整个 { code, message, data } 信封返回，页面是判断 res.code === 200 的，所以这里保持一样的格式。

// 把后端返回的一条库存整理成页面表格要用的字段
// 页面表格列读的是 stock / minStock / status / warehouse 这些，
// 后端实体 Stock 里对应的是 quantity / warningThreshold / institutionName，所以在这里转一下
function formatStock(item) {
  return {
    ...item,
    stock: item.quantity,              // 当前库存
    minStock: item.warningThreshold,   // 最低库存（后端叫预警阈值）
    // 库存数量小于等于预警阈值就算预警，页面用这个给库存和状态标红
    status: item.quantity <= item.warningThreshold ? 'warning' : 'normal',
    // 页面「仓库」这一列，后端没有专门的仓库字段，就先拿机构名称显示
    warehouse: item.institutionName
    // 备注：maxStock、categoryName、location 后端目前没返回，先留空
  }
}

// 分页查询库存列表
// 页面 loadData 传的是 { drugName, categoryName, status, warehouse, pageNum, pageSize }，
// 后端 StockQueryDTO 只认 drugId / institutionId / warningOnly（还有分页），所以这里做下映射，
// 页面读的是 res.data.list 和 res.data.total，而后端分页返回的是 { total, records, ... }，
// 所以下面把 records 映射成 list 再返回（写法参考 api/auth.js）
export function getInventoryList(params) {
  return request({
    url: '/stocks/page',
    method: 'get',
    params: {
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      // 药品名称/编码：后端有 keyword 字段，就先塞进去
      keyword: params.drugName || undefined,
      // 页面选「库存预警」时，后端用 warningOnly 过滤
      warningOnly: params.status === 'warning' ? true : undefined
      // 备注：categoryName、warehouse 后端暂时不支持按这两个筛，先不传
    }
  }).then(res => {
    if (res.code === 200) {
      return {
        code: 200,
        message: res.message,
        data: {
          list: res.data.records.map(formatStock), // records -> list
          total: res.data.total,
          pageNum: res.data.pageNum,
          pageSize: res.data.pageSize
        }
      }
    }
    return res
  })
}

// 药品入库
// 页面入库弹窗表单是 { id, drugName, currentStock, unit, quantity, type, source, remark }，
// 后端要的是 StockOperationDTO（drugId、institutionId、quantity、operationType、remark 等），
// 这里把表单字段转成后端 DTO；入库的 operationType 固定是 1（后端 Constants.STOCK_OP_IN）
export function stockIn(data) {
  return request({
    url: '/stocks/in',
    method: 'post',
    data: {
      drugId: data.drugId,
      institutionId: data.institutionId,
      operationType: 1,            // 1 = 入库
      quantity: data.quantity,
      remark: data.remark
    }
  })
}

// 药品出库
// 表单字段和入库差不多，就是出库的 operationType 固定是 2（Constants.STOCK_OP_OUT）
export function stockOut(data) {
  return request({
    url: '/stocks/out',
    method: 'post',
    data: {
      drugId: data.drugId,
      institutionId: data.institutionId,
      operationType: 2,            // 2 = 出库
      quantity: data.quantity,
      remark: data.remark
    }
  })
}

// 药品调拨（仓库/机构之间调货）
// 页面表单是 { id, drugName, fromWarehouse, toWarehouse, currentStock, quantity, remark }，
// 后端要调出机构ID 和 调入机构ID，字段名对应 fromInstitutionId / toInstitutionId，
// 调拨的 operationType 固定是 3（Constants.STOCK_OP_TRANSFER）
export function stockTransfer(data) {
  return request({
    url: '/stocks/transfer',
    method: 'post',
    data: {
      drugId: data.drugId,
      // DTO 的 institutionId 带了 @NotNull，但调拨逻辑实际用的是 fromInstitutionId/toInstitutionId，
      // 后端 Service 不读 institutionId，这里塞 fromInstitutionId 凑过校验
      institutionId: data.fromInstitutionId,
      fromInstitutionId: data.fromInstitutionId,
      toInstitutionId: data.toInstitutionId,
      operationType: 3,            // 3 = 调拨
      quantity: data.quantity,
      remark: data.remark
    }
  })
}

// 库存预警列表
// 后端 /stocks/warning 直接返回一个数组（Result<List<Stock>>），
// 页面读的是 res.data（数组）和 res.data.length，所以这里只把每条记录的字段转一下，外层信封不动
export function getWarningList() {
  return request({
    url: '/stocks/warning',
    method: 'get'
  }).then(res => {
    if (res.code === 200) {
      res.data = res.data.map(formatStock)
    }
    return res
  })
}
