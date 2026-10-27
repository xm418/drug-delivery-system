<template>
  <!-- 医疗机构管理页面容器 -->
  <div class="page-container">
    <!-- 左右布局行 - 左边树右边列表 -->
    <el-row :gutter="20">
      <!-- 左侧树形结构区域 - 占6份 -->
      <el-col :span="6">
        <div class="tree-card">
          <!-- 卡片标题 -->
          <div class="card-header">
            <span class="card-title">机构树形结构</span>
          </div>
          <!-- 树节点搜索框 - 带搜索图标前缀 -->
          <el-input
            v-model="filterText"
            placeholder="搜索机构名称"
            clearable
            size="small"
            style="margin-bottom: 12px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <!-- 机构树组件 - 默认展开全部，高亮当前节点 -->
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            :filter-node-method="filterNode"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <!-- 自定义树节点模板 - 根据层级显示不同图标 -->
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <!-- 一级用办公楼图标 -->
                <el-icon v-if="data.level === 1"><OfficeBuilding /></el-icon>
                <!-- 二级用加号图标 -->
                <el-icon v-else-if="data.level === 2"><Plus /></el-icon>
                <!-- 三级及以下用定位图标 -->
                <el-icon v-else><LocationFilled /></el-icon>
                <span class="node-label">{{ node.label }}</span>
              </span>
            </template>
          </el-tree>
        </div>
      </el-col>

      <!-- 右侧列表区域 - 占18份 -->
      <el-col :span="18">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true" :model="searchForm" @submit.prevent>
            <!-- 机构名称输入框 -->
            <el-form-item label="机构名称">
              <el-input v-model="searchForm.name" placeholder="请输入机构名称" clearable style="width: 200px" />
            </el-form-item>
            <!-- 机构级别下拉选择 -->
            <el-form-item label="级别">
              <el-select v-model="searchForm.level" placeholder="请选择级别" clearable style="width: 140px">
                <el-option label="县级医院" :value="1" />
                <el-option label="乡镇卫生院" :value="2" />
                <el-option label="村卫生室" :value="3" />
              </el-select>
            </el-form-item>
            <!-- 搜索重置按钮 -->
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 操作工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <!-- 新增机构按钮 -->
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增机构
            </el-button>
          </div>
        </div>

        <!-- 机构列表表格 -->
        <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="name" label="机构名称" min-width="180" />
          <!-- 机构级别列 - 不同级别不同颜色标签 -->
          <el-table-column prop="levelName" label="机构级别" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getLevelType(row.level)" size="small">{{ row.levelName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="principal" label="负责人" width="100" />
          <el-table-column prop="phone" label="联系电话" width="140" />
          <!-- 地址列 - 超出显示tooltip -->
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
          <!-- 状态列 - 启用/禁用 -->
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 操作列 - 编辑和删除 -->
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="loadData"
            @current-change="loadData"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 新增/编辑机构弹窗 - 共用一个弹窗，通过isEdit区分 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <!-- 机构表单 - 带验证 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="机构名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入机构名称" />
        </el-form-item>
        <!-- 机构级别选择 -->
        <el-form-item label="机构级别" prop="level">
          <el-select v-model="formData.level" placeholder="请选择级别" style="width: 100%">
            <el-option label="县级医院" :value="1" />
            <el-option label="乡镇卫生院" :value="2" />
            <el-option label="村卫生室" :value="3" />
          </el-select>
        </el-form-item>
        <!-- 上级机构选择 - 只有二级三级机构才显示，用树选择器 -->
        <el-form-item label="上级机构" prop="parentId" v-if="formData.level > 1">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择上级机构"
            style="width: 100%"
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="principal">
          <el-input v-model="formData.principal" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
        <!-- 状态单选 - 启用/禁用 -->
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 引入vue组合式API - computed用来算弹窗标题，watch监听搜索框过滤树
import { ref, reactive, computed, onMounted, watch } from 'vue'
// 引入element-plus消息和确认框
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入机构管理相关API
import { getInstitutionTree, getInstitutionList, addInstitution, updateInstitution, deleteInstitution } from '@/api/institution'

// 表格加载状态
const loading = ref(false)
// 提交按钮loading
const submitLoading = ref(false)
// 弹窗显示控制
const dialogVisible = ref(false)
// 是否编辑模式 - true编辑 false新增
const isEdit = ref(false)
// 表单ref引用
const formRef = ref(null)
// 树组件ref引用 - 用来调用filter方法
const treeRef = ref(null)
// 树节点搜索过滤文字
const filterText = ref('')

// 树形结构数据
const treeData = ref([])
// 表格列表数据
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  name: '',    // 机构名称
  level: ''    // 机构级别
})

// 分页参数
const pagination = reactive({
  pageNum: 1,     // 当前页
  pageSize: 10,   // 每页条数
  total: 0        // 总条数
})

// 机构表单数据
const formData = reactive({
  id: null,           // 机构id
  name: '',           // 机构名称
  level: 1,           // 机构级别，默认1县级
  parentId: null,     // 上级机构id
  principal: '',      // 负责人
  phone: '',          // 联系电话
  address: '',        // 地址
  status: 1           // 状态，默认1启用
})

// 表单验证规则 - 这块参考了element-plus官方文档
const formRules = {
  name: [
    { required: true, message: '请输入机构名称', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择机构级别', trigger: 'change' }
  ],
  principal: [
    { required: true, message: '请输入负责人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
  // TODO: 后面加个手机号格式验证
}

// 树组件的props配置 - 指定children和label字段名
const treeProps = {
  children: 'children',
  label: 'name'
}

// 弹窗标题 - 根据isEdit动态计算
const dialogTitle = computed(() => isEdit.value ? '编辑机构' : '新增机构')

// 根据级别返回对应的tag类型
const getLevelType = (level) => {
  const map = { 1: '', 2: 'primary', 3: 'success' }
  return map[level] || 'info'
}

// 加载树形结构数据
const loadTreeData = async () => {
  const res = await getInstitutionTree()
  // console.log('机构树数据：', res.data) // 调试用
  if (res.code === 200) {
    treeData.value = res.data
  }
}

// 加载机构列表数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getInstitutionList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 树节点过滤方法 - 根据名称模糊匹配
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

// 监听搜索框文字变化 - 实时过滤树节点
// 这块参考了element-plus tree组件的过滤功能文档
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

// 树节点点击事件 - 点击后按机构名称搜索
const handleNodeClick = (data) => {
  searchForm.name = data.name
  handleSearch()
}

// 搜索按钮 - 重置页码然后加载
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置按钮 - 清空搜索条件和树过滤
const handleReset = () => {
  searchForm.name = ''
  searchForm.level = ''
  filterText.value = ''
  pagination.pageNum = 1
  loadData()
}

// 新增机构按钮 - 重置表单数据然后弹窗
const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    name: '',
    level: 1,
    parentId: null,
    principal: '',
    phone: '',
    address: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑按钮 - 把行数据填到表单里
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 删除按钮 - 弹确认框然后调删除接口
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除机构 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteInstitution(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()       // 重新加载列表
      loadTreeData()   // 重新加载树
    }
  }).catch(() => {})
}

// 表单提交 - 新增或编辑共用
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 先做表单验证
    await formRef.value.validate()
    submitLoading.value = true
    
    let res
    if (isEdit.value) {
      // 编辑模式调更新接口
      res = await updateInstitution(formData.id, formData)
    } else {
      // 新增模式调新增接口
      res = await addInstitution(formData)
    }
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
      loadTreeData()
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

// 弹窗关闭回调 - 重置表单
const handleDialogClosed = () => {
  formRef.value?.resetFields()
}

// 页面挂载完成 - 加载树和列表数据
onMounted(() => {
  loadTreeData()
  loadData()
})

// TODO: 机构导入导出功能还没做，毕设答辩前尽量加上
// TODO: 机构下面的人员管理功能也得做，现在只能管机构信息
</script>

<style scoped>
/* 树卡片样式 - 白色背景圆角内边距 */
.tree-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 16px;
}

/* 卡片头部 - 底部留间距 */
.card-header {
  margin-bottom: 12px;
}

/* 卡片标题 - 加粗加大 */
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 自定义树节点样式 - 图标和文字水平排列 */
.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

/* 节点标签 - 占满剩余空间 */
.node-label {
  flex: 1;
}

/* 搜索栏样式 */
.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 工具栏样式 */
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 分页容器 - 右对齐 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
