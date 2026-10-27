import request from '@/utils/request'

// 医疗机构相关的接口
// 原来这里是用 setTimeout 返回写死的 mockInstitutions 数组，
// 但页面早就改成调真接口了，函数名对不上页面一打开就报错，
// 所以这里全部改成真调后端（后端接口都在 InstitutionController 里）

// 机构级别对应中文名，页面表格那一列显示的就是 levelName
const LEVEL_NAME_MAP = {
  1: '县级医院',
  2: '乡镇卫生院',
  3: '村卫生室'
}

// 后端实体里的字段名是 contactPerson、contactPhone，
// 页面表格和表单里用的是 principal、phone，还有 levelName，
// 所以两边来回转的时候要做个字段映射

// 后端 -> 页面（列表/详情用）
function toPageItem(item) {
  return {
    ...item,
    levelName: LEVEL_NAME_MAP[item.level] || '',
    principal: item.contactPerson,
    phone: item.contactPhone
  }
}

// 页面 -> 后端（新增/修改提交用）
function toBackendItem(data) {
  const payload = { ...data }
  // 表单里叫 principal、phone，后端要的是 contactPerson、contactPhone
  payload.contactPerson = data.principal
  payload.contactPhone = data.phone
  // 这几个字段后端表里没有，去掉免得传过去
  delete payload.principal
  delete payload.phone
  delete payload.levelName
  return payload
}

// 后端返回的是平铺列表，这里按 parentId 自己拼成三级树
// （县医院 -> 乡镇卫生院 -> 村卫生室），树组件要用 children
function buildTree(list) {
  const map = {}
  const roots = []
  // 先把每条数据变成带 children 的节点，用 id 做个索引方便找父级
  list.forEach((item) => {
    map[item.id] = { ...toPageItem(item), children: [] }
  })
  // 第二遍把节点挂到父节点下面
  list.forEach((item) => {
    const node = map[item.id]
    const parent = item.parentId ? map[item.parentId] : null
    if (parent) {
      parent.children.push(node)
    } else {
      // 没有父级（parentId 是 0 或者 null）就当根节点
      roots.push(node)
    }
  })
  // 叶子节点把空的 children 去掉，不然树组件会留个展开的小箭头
  const clearEmpty = (nodes) => {
    nodes.forEach((node) => {
      if (node.children.length === 0) {
        delete node.children
      } else {
        clearEmpty(node.children)
      }
    })
  }
  clearEmpty(roots)
  return roots
}

// 获取机构树形结构（三级树，左侧那个 el-tree 用）
// 后端没有现成的 tree 接口，就是查全部机构（/institutions/list）
// 返回的是平铺列表，我在上面 buildTree 里拼成树
export function getInstitutionTree() {
  return request({
    url: '/institutions/list',
    method: 'get'
  }).then((res) => {
    if (res.code === 200) {
      return {
        code: 200,
        message: res.message,
        data: buildTree(res.data || [])
      }
    }
    return res
  })
}

// 获取机构列表（分页，右侧表格用）
// 后端分页接口是 /institutions/page，参数走 PageQueryDTO，
// 关键字字段后端叫 keyword，页面搜索框里是 name；级别是单独一个 level 参数
// 后端返回 PageResult{ total, records, ... }，页面读的是 res.data.list，
// 所以这里把 records 映射成 list，顺便把字段也映射一下
export function getInstitutionList(params) {
  const query = {
    pageNum: params.pageNum,
    pageSize: params.pageSize,
    keyword: params.name
  }
  // 级别为空的时候不要传，不然后端 Integer 转空字符串会报错
  if (params.level !== '' && params.level !== null && params.level !== undefined) {
    query.level = params.level
  }
  return request({
    url: '/institutions/page',
    method: 'get',
    params: query
  }).then((res) => {
    if (res.code === 200) {
      const data = res.data
      return {
        code: 200,
        message: res.message,
        data: {
          list: (data.records || []).map(toPageItem),
          total: data.total,
          pageNum: data.pageNum,
          pageSize: data.pageSize
        }
      }
    }
    return res
  })
}

// 新增机构
// 页面传的是表单 formData，字段名和后端对不上，先转一下再提交
export function addInstitution(data) {
  return request({
    url: '/institutions',
    method: 'post',
    data: toBackendItem(data)
  })
}

// 修改机构
// 页面调用是 updateInstitution(formData.id, formData)，
// 后端 PUT /institutions 的请求体里自带 id，所以直接把 data 传过去就行
export function updateInstitution(id, data) {
  const payload = toBackendItem(data)
  // 兜底，万一 data 里的 id 没值就用传进来的
  if (!payload.id) {
    payload.id = id
  }
  return request({
    url: '/institutions',
    method: 'put',
    data: payload
  })
}

// 删除机构
export function deleteInstitution(id) {
  return request({
    url: `/institutions/${id}`,
    method: 'delete'
  })
}
