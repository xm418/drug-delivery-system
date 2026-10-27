import request from '@/utils/request'

// 首页相关的接口，之前这里是用 setTimeout 写死的假数据，
// 后来改成真调后端了，数据都是从数据库里查出来的

// 首页上面四个统计卡片的数据（药品总数、库存总量、今日订单、待配送任务）
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

// 最近7天的订单趋势，折线图用
export function getOrderTrend() {
  return request({
    url: '/dashboard/order-trend',
    method: 'get'
  })
}

// 药品分类占比，环形图用
export function getDrugCategoryRatio() {
  return request({
    url: '/dashboard/category-ratio',
    method: 'get'
  })
}

// 配送任务的状态统计（首页暂时没用到，先留着）
export function getDeliveryStats() {
  return request({
    url: '/dashboard/delivery-stats',
    method: 'get'
  })
}

// 库存预警列表，首页下面那个表格用
export function getStockWarningList() {
  return request({
    url: '/dashboard/stock-warning',
    method: 'get'
  })
}

// 最近的订单，首页右边那个表格用
export function getRecentOrders() {
  return request({
    url: '/dashboard/recent-orders',
    method: 'get'
  })
}
