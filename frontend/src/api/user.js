import request from '@/utils/request'

// 用户管理相关的接口
// 之前这里是用 setTimeout 写死的假数据，页面看着有数据其实根本没连后端，
// 后来 user/index.vue 改成真调后端接口了，这里就得跟着改，
// 把那些 mock 数组和 setTimeout 全删掉，直接请求后端

// 前端角色下拉用的是英文编码（admin、manager 这些），
// 但后端 sys_user 表里存的是数字的 roleId，所以这里做一层对应关系
const roleCodeToId = {
  admin: 1,       // 超级管理员
  manager: 2,     // 配送管理员
  pharmacist: 3,  // 药剂师
  doctor: 4,      // 医生
  nurse: 5,       // 护士
  driver: 6,      // 配送员
  finance: 7      // 财务
}

// 反过来，后端返回的 roleId 要转成前端下拉认的编码，编辑弹窗回显的时候要用
const roleIdToCode = {
  1: 'admin',
  2: 'manager',
  3: 'pharmacist',
  4: 'doctor',
  5: 'nurse',
  6: 'driver',
  7: 'finance'
}

// 分页查询用户列表
export function getUserList(params) {
  // 页面传过来的是 { username, role, status, pageNum, pageSize }
  // 后端要的是 { keyword, roleId, status, pageNum, pageSize }
  const query = {
    pageNum: params.pageNum,
    pageSize: params.pageSize
  }
  // 用户名/姓名关键词，后端入参叫 keyword
  if (params.username) {
    query.keyword = params.username
  }
  // 角色编码转成 roleId
  if (params.role) {
    query.roleId = roleCodeToId[params.role]
  }
  // 状态（0或1），空的就不传
  if (params.status !== '' && params.status !== null && params.status !== undefined) {
    query.status = params.status
  }

  return request({
    url: '/users/page',
    method: 'get',
    params: query
  }).then(res => {
    // 后端分页返回的是 PageResult：{ total, records, pageNum, pageSize }
    // 页面读的是 res.data.list，所以这里把 records 转成 list 再返回
    if (res.code === 200 && res.data) {
      res.data.list = res.data.records || []
      // 顺手给每条记录补个前端下拉用的 role 编码，编辑的时候表单能自动选中
      res.data.list.forEach(item => {
        item.role = roleIdToCode[item.roleId] || ''
      })
    }
    return res
  })
}

// 新增用户
export function addUser(data) {
  // 页面的 formData 里角色字段叫 role，后端要 roleId，这里转一下再提交
  const body = { ...data }
  if (body.role) {
    body.roleId = roleCodeToId[body.role]
  }
  delete body.role

  return request({
    url: '/users',
    method: 'post',
    data: body
  })
}

// 修改用户
// 后端是 PUT /users，id 放在请求体里就行，不用拼在 url 上
export function updateUser(id, data) {
  const body = { ...data }
  if (body.role) {
    body.roleId = roleCodeToId[body.role]
  }
  delete body.role
  body.id = id

  return request({
    url: '/users',
    method: 'put',
    data: body
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 重置密码
// 管理员重置，后端会把密码重置成 123456
export function resetPassword(id) {
  return request({
    url: `/users/${id}/reset-password`,
    method: 'put'
  }).then(res => {
    // 后端默认返回的提示是"操作成功"，这里换成和确认框里一样的提示，看着清楚点
    if (res.code === 200) {
      res.message = '密码已重置为 123456'
    }
    return res
  })
}
