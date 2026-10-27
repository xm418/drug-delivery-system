import request from '@/utils/request'

// 药品分类相关的接口
// 之前这里是用 setTimeout 写死的假数据（mockCategories 数组），
// 后来页面改成真调后端了，所以把假数据全删掉，改成请求后端接口

// 获取分类分页列表
// 页面传的是 { name, status, pageNum, pageSize }，
// 后端分页接口只认 keyword/pageNum/pageSize，是按分类名称模糊查的，
// 所以这里把 name 转成 keyword 传过去（status 后端暂时不支持过滤，就先不传了）
export function getCategoryList(params) {
  return request({
    url: '/drug-categories/page',
    method: 'get',
    params: {
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      keyword: params.name || undefined
    }
  }).then(res => {
    // 后端返回的是 PageResult { total, records, pageNum, pageSize }，
    // 页面里读的是 res.data.list，所以把 records 改名成 list 给它
    if (res.code === 200) {
      const records = res.data.records || []
      return {
        code: 200,
        message: res.message,
        data: {
          // 后端实体字段是 categoryName/categoryCode/remark，
          // 分类页面表格读的是 name/code/description，顺便转成页面用的字段名
          list: records.map(item => ({
            id: item.id,
            name: item.categoryName,
            code: item.categoryCode,
            description: item.remark,
            sort: item.sort,
            status: item.status,
            parentId: item.parentId,
            parentName: item.parentName,
            createTime: item.createTime
          })),
          total: res.data.total,
          pageNum: res.data.pageNum,
          pageSize: res.data.pageSize
        }
      }
    }
    return res
  })
}

// 获取所有分类（药品页面的下拉框用）
// 后端这个接口已经只返回 status=1（启用）的分类了，
// 页面里读的是 categoryName 字段，后端返回的正好就是 categoryName，直接返回不用转换
export function getAllCategoryList() {
  return request({
    url: '/drug-categories/list',
    method: 'get'
  })
}

// 新增分类
// 页面表单字段是 name/code/description，后端接收的是 DrugCategory，
// 字段是 categoryName/categoryCode/remark，所以提交时要转一下
export function addCategory(data) {
  return request({
    url: '/drug-categories',
    method: 'post',
    data: {
      categoryName: data.name,
      categoryCode: data.code,
      sort: data.sort,
      remark: data.description,
      status: data.status
    }
  })
}

// 编辑分类
// 页面调用的时候传了 id 和整个表单对象，后端 PUT 是按 body 里的 id 更新的，
// 所以这里把 id 一起放进请求体，其它字段和新增一样要转成后端字段名
export function updateCategory(id, data) {
  return request({
    url: '/drug-categories',
    method: 'put',
    data: {
      id: id,
      categoryName: data.name,
      categoryCode: data.code,
      sort: data.sort,
      remark: data.description,
      status: data.status
    }
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/drug-categories/${id}`,
    method: 'delete'
  })
}
