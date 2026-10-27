import request from '@/utils/request'

// 药品相关的接口
// 这个文件以前是用 setTimeout 返回写死的假数据，页面早就改成调后端了，
// 所以现在全部改成真调后端接口，mock 数组和 setTimeout 都删掉了

// 分页查询药品列表（药品管理页面用）
// 后端接口：GET /drugs/page
// 入参：pageNum、pageSize、keyword（按名称或编码模糊搜）、categoryId、status
// 注意：后端分页返回的字段是 records，但页面里读的是 list，所以这里转一下
export function getDrugPage(params) {
  return request({
    url: '/drugs/page',
    method: 'get',
    params
  }).then(res => {
    if (res.code === 200) {
      return {
        code: 200,
        message: res.message,
        data: {
          list: res.data.records,
          total: res.data.total,
          pageNum: res.data.pageNum,
          pageSize: res.data.pageSize
        }
      }
    }
    return res
  })
}

// 根据 id 查询药品详情（编辑弹窗回显用）
// 后端接口：GET /drugs/{id}
export function getDrugById(id) {
  return request({
    url: '/drugs/' + id,
    method: 'get'
  })
}

// 获取药品列表（创建订单时选药品的下拉用）
// 后端接口：GET /drugs/page
// order 页面里读的字段是 name，但后端实体里叫 drugName，
// 所以这里顺便补一个 name 字段上去，省得再改页面
export function getDrugList(params) {
  return request({
    url: '/drugs/page',
    method: 'get',
    params
  }).then(res => {
    if (res.code === 200) {
      const list = res.data.records.map(item => ({
        ...item,
        name: item.drugName
      }))
      return {
        code: 200,
        message: res.message,
        data: {
          list,
          total: res.data.total,
          pageNum: res.data.pageNum,
          pageSize: res.data.pageSize
        }
      }
    }
    return res
  })
}

// 新增药品
// 后端接口：POST /drugs，请求体是整个药品对象
export function addDrug(data) {
  return request({
    url: '/drugs',
    method: 'post',
    data
  })
}

// 修改药品
// 后端接口：PUT /drugs，请求体是整个药品对象（里面带 id）
export function updateDrug(data) {
  return request({
    url: '/drugs',
    method: 'put',
    data
  })
}

// 删除药品（后端做的是逻辑删除，不是真删）
// 后端接口：DELETE /drugs/{id}
export function deleteDrug(id) {
  return request({
    url: '/drugs/' + id,
    method: 'delete'
  })
}
