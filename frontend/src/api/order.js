import request from '@/utils/request'

// 订单相关的接口
// 之前这里是用 setTimeout 写的假数据，页面早就改成调真接口了，
// 但是函数名对不上一直报错，所以现在整个改成真调后端

// 后端订单状态存的是数字（看 Constants.java），页面里用的是英文 key，
// 这里做一下互相转换，不然页面上的判空和标签颜色都不对
// 后端定义：0-待审核 1-已审核 2-已驳回 3-配送中 4-已完成 5-已取消
const statusKeyMap = {
  0: 'pending',
  1: 'approved',
  2: 'rejected',
  3: 'delivering',
  4: 'completed',
  5: 'cancelled'
}

// 反过来的映射，英文 key -> 数字，搜索传参的时候要用
const statusNumMap = {
  pending: 0,
  approved: 1,
  rejected: 2,
  delivering: 3,
  completed: 4,
  cancelled: 5
}

// 状态显示文字，后端没返回状态名称，我自己按 key 拼一个
const statusNameMap = {
  pending: '待审核',
  approved: '已审核',
  rejected: '已驳回',
  delivering: '配送中',
  completed: '已完成',
  cancelled: '已取消'
}

// 把后端返回的一条订单整理成页面表格要的样子
// 页面表格里读的是 status（英文）、statusName、createBy 这些字段
const formatOrder = (order) => {
  const statusKey = statusKeyMap[order.status]
  return {
    ...order,
    status: statusKey,
    statusName: statusNameMap[statusKey] || '',
    // 后端下单人字段叫 username，页面表格列读的是 createBy
    createBy: order.username,
    // 审核人、审核时间也对一下字段名
    auditBy: order.approveByName,
    auditTime: order.approveTime
  }
}

// 整理订单明细，后端明细表字段是 unitPrice / totalPrice，
// 详情页表格里读的是 price / amount，这里映射一下
const formatOrderItem = (item) => ({
  ...item,
  price: item.unitPrice,
  amount: item.totalPrice
})

// 获取订单列表（分页）
// 后端接口：GET /orders/page
// 后端返回 PageResult { total, records, pageNum, pageSize }，
// 页面读的是 res.data.list，所以这里把 records 改成 list
export function getOrderList(params) {
  const query = {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    orderNo: params.orderNo,
    institutionId: params.institutionId
  }
  // 状态是英文 key，后端要数字，转一下再传
  if (params.status) {
    query.status = statusNumMap[params.status]
  }
  return request({
    url: '/orders/page',
    method: 'get',
    params: query
  }).then(res => {
    if (res.code === 200) {
      const page = res.data
      return {
        code: 200,
        message: res.message,
        data: {
          list: (page.records || []).map(formatOrder),
          total: page.total,
          pageNum: page.pageNum,
          pageSize: page.pageSize
        }
      }
    }
    return res
  })
}

// 获取订单详情（包含药品明细）
// 后端接口：GET /orders/{id}/detail
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}/detail`,
    method: 'get'
  }).then(res => {
    if (res.code === 200 && res.data) {
      const order = formatOrder(res.data)
      order.items = (res.data.items || []).map(formatOrderItem)
      return {
        code: 200,
        message: res.message,
        data: order
      }
    }
    return res
  })
}

// 创建订单
// 后端接口：POST /orders
// 请求体只认 institutionId、remark、items（明细里只要 drugId 和 quantity），
// 价格、总金额这些后端自己会根据药品表算，我这边不用传
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data: {
      institutionId: data.institutionId,
      remark: data.remark,
      items: (data.items || []).map(item => ({
        drugId: item.drugId,
        quantity: item.quantity
      }))
    }
  })
}

// 审核订单
// 后端接口：PUT /orders/{id}/approve
// 页面传的是 { status: 'approved' | 'rejected', remark }，
// 后端要的是 approved 参数（1通过 0驳回），remark 走 query 参数
export function auditOrder(id, data) {
  return request({
    url: `/orders/${id}/approve`,
    method: 'put',
    params: {
      approved: data.status === 'approved' ? 1 : 0,
      remark: data.remark
    }
  })
}

// 取消订单
// 后端接口：PUT /orders/{id}/cancel
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'put'
  })
}
