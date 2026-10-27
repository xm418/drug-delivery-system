import request from '@/utils/request'

// 配送任务相关的接口
// 之前这里是用 setTimeout 返回一堆写死的假数据，页面早就改成按后端真实接口来写了，
// 但是函数名一直对不上，页面一打开就报错，所以这里全部换成真调后端，
// 函数名和 delivery/index.vue 里 import 的那几个保持一致

// 后端 delivery_task 表里的 status 存的是数字，页面里用的是文字状态，这里做一下互转
// 注意：状态值是跟着后端 Constants.java 走的：0-待配送 1-配送中 2-已送达 3-已完成
// （实体类上面那行注释是老的，写错了，别按那个来）
const statusKeyToCode = {
  pending: 0,
  delivering: 1,
  completed: 3,
  cancelled: 4
}

// 2(已送达)和3(已完成)页面都归成"已完成"这一类来显示，不然面板上的标签颜色对不上
const statusCodeToKey = {
  0: 'pending',
  1: 'delivering',
  2: 'completed',
  3: 'completed',
  4: 'cancelled'
}

const statusNameMap = {
  0: '待配送',
  1: '配送中',
  2: '已送达',
  3: '已完成',
  4: '已取消'
}

// 后端返回的字段名和页面表格用的不太一样，这里统一整理成页面要的那一套
function formatTask(task) {
  if (!task) {
    return task
  }
  return {
    ...task,
    // 后端收货机构名是 toInstitutionName，页面表格列 prop 是 institutionName
    institutionName: task.toInstitutionName,
    // 后端配送员是 deliveryPersonName / deliveryPhone，页面用的是 driver / driverPhone
    driver: task.deliveryPersonName,
    driverPhone: task.deliveryPhone,
    // 后端的发货时间、收货时间，页面上显示成 startTime / endTime
    startTime: task.deliveryTime,
    endTime: task.receiveTime,
    // 把数字状态转成页面判断状态用的字符串，顺便带上中文状态名
    status: statusCodeToKey[task.status] || '',
    statusName: statusNameMap[task.status] || ''
  }
}

// 分页查询配送任务列表
export function getDeliveryTaskList(params) {
  // 后端 DeliveryQueryDTO 只认数字状态，页面传过来的是 'pending' 这种文字，先转一下
  const query = {
    pageNum: params.pageNum,
    pageSize: params.pageSize
  }
  if (params.status) {
    query.status = statusKeyToCode[params.status]
  }
  return request({
    url: '/delivery-tasks/page',
    method: 'get',
    params: query
  }).then(res => {
    // 后端分页返回的是 records，页面读的是 data.list，所以在这里转一下
    if (res.code === 200) {
      const data = res.data
      return {
        code: 200,
        message: res.message,
        data: {
          list: (data.records || []).map(formatTask),
          total: data.total,
          pageNum: data.pageNum,
          pageSize: data.pageSize
        }
      }
    }
    return res
  })
}

// 创建配送任务
// 后端是用 @RequestParam 接参的，不是 json 请求体，所以要放到 params 里发
export function createDeliveryTask(data) {
  return request({
    url: '/delivery-tasks',
    method: 'post',
    params: {
      orderId: data.orderId,
      fromInstitutionId: data.fromInstitutionId,
      toInstitutionId: data.toInstitutionId,
      deliveryPersonName: data.deliveryPersonName,
      deliveryPhone: data.deliveryPhone,
      remark: data.remark
    }
  })
}

// 修改配送任务状态
// 页面传的是 'delivering' 这种文字，后端 /status 接口要的是数字状态，转一下再发
export function updateTaskStatus(id, status) {
  return request({
    url: `/delivery-tasks/${id}/status`,
    method: 'put',
    params: {
      status: statusKeyToCode[status]
    }
  })
}

// 分配配送员
// 后端没有单独"分配配送员"的接口，能推进任务的只有开始配送 /start，
// 而且分配的时候任务正好是待发货状态，符合 /start 的前提，所以这里就复用 /start，
// 状态会从待发货变成配送中（页面传的 data 里是配送员姓名/电话，
// 但后端没有接口可以单独存这两个字段，所以这里没用到）
export function assignDriver(id, data) {
  return request({
    url: `/delivery-tasks/${id}/start`,
    method: 'put'
  })
}
