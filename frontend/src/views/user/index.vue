<template>
  <!-- 用户管理页面容器 -->
  <div class="page-container">
    <!-- 搜索栏 - 上面的搜索条件区域 -->
    <div class="search-bar">
      <!-- 搜索表单，横向排列 -->
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <!-- 用户名/姓名搜索输入框 -->
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名/姓名" clearable style="width: 200px" />
        </el-form-item>
        <!-- 角色下拉选择 -->
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable style="width: 160px">
            <el-option label="超级管理员" value="admin" />
            <el-option label="配送管理员" value="manager" />
            <el-option label="药剂师" value="pharmacist" />
            <el-option label="医生" value="doctor" />
            <el-option label="护士" value="nurse" />
            <el-option label="配送员" value="driver" />
            <el-option label="财务" value="finance" />
          </el-select>
        </el-form-item>
        <!-- 状态下拉选择，启用/禁用 -->
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <!-- 搜索和重置按钮 -->
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

    <!-- 操作栏 - 放新增按钮的地方 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- 新增用户按钮 -->
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
      </div>
    </div>

    <!-- 用户列表表格 - 主要数据展示区 -->
    <!-- TODO: 后面可以加个头像列，看起来更形象一点 -->
    <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
      <!-- 序号列 -->
      <el-table-column type="index" label="序号" width="60" align="center" />
      <!-- 用户名 -->
      <el-table-column prop="username" label="用户名" min-width="120" />
      <!-- 真实姓名 -->
      <el-table-column prop="realName" label="姓名" min-width="100" />
      <!-- 角色名称 -->
      <el-table-column prop="roleName" label="角色" min-width="120" />
      <!-- 手机号 -->
      <el-table-column prop="phone" label="手机号" min-width="130" />
      <!-- 邮箱，太长显示省略号 -->
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <!-- 状态列，启用绿色，禁用红色 -->
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 创建时间 -->
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <!-- 操作列，固定在右边 -->
      <el-table-column label="操作" width="260" fixed="right" align="center">
        <template #default="{ row }">
          <!-- 编辑按钮 -->
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <!-- 重置密码按钮 -->
          <el-button type="warning" link size="small" @click="handleResetPwd(row)">重置密码</el-button>
          <!-- 删除按钮 -->
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 - 表格下面的分页组件 -->
    <!-- 分页逻辑我自己写的，用element-plus的分页组件配合后端接口 -->
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

    <!-- 新增/编辑用户弹窗 - 同一个弹窗，新增和编辑共用 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <!-- 用户表单 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <!-- 用户名输入框，编辑时不可修改 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <!-- 姓名输入框 -->
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <!-- 角色选择下拉框 -->
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" value="admin" />
            <el-option label="配送管理员" value="manager" />
            <el-option label="药剂师" value="pharmacist" />
            <el-option label="医生" value="doctor" />
            <el-option label="护士" value="nurse" />
            <el-option label="配送员" value="driver" />
            <el-option label="财务" value="finance" />
          </el-select>
        </el-form-item>
        <!-- 手机号输入框 -->
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <!-- 邮箱输入框 -->
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <!-- 状态单选，只有编辑的时候才显示，新增默认启用 -->
        <el-form-item label="状态" prop="status" v-if="isEdit">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 引入vue的API
import { ref, reactive, computed, onMounted } from 'vue'
// element-plus的消息提示和确认框
import { ElMessage, ElMessageBox } from 'element-plus'
// 用户相关的接口函数
import { getUserList, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'

// 表格加载状态
const loading = ref(false)
// 提交表单时的loading
const submitLoading = ref(false)
// 弹窗显示状态
const dialogVisible = ref(false)
// 是否是编辑模式（true是编辑，false是新增）
const isEdit = ref(false)
// 表单的ref引用，用来做校验
const formRef = ref(null)

// 搜索表单数据
const searchForm = reactive({
  username: '',   // 用户名/姓名关键词
  role: '',       // 角色
  status: ''      // 状态
})

// 分页数据
const pagination = reactive({
  pageNum: 1,     // 当前页
  pageSize: 10,   // 每页条数
  total: 0        // 总条数
})

// 表格数据列表
const tableData = ref([])

// 表单数据 - 新增/编辑用户用
const formData = reactive({
  id: null,       // 用户ID
  username: '',   // 用户名
  realName: '',   // 真实姓名
  role: '',       // 角色
  phone: '',      // 手机号
  email: '',      // 邮箱
  status: 1       // 状态，默认启用
})

// 表单校验规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    // 手机号正则校验，1开头，第二位3-9，后面9位数字
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 计算属性：弹窗标题，根据isEdit判断是新增还是编辑
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

// 角色编码和名称的映射
// 这个跟后端的角色枚举要对应上
const roleNameMap = {
  admin: '超级管理员',
  manager: '配送管理员',
  pharmacist: '药剂师',
  doctor: '医生',
  nurse: '护士',
  driver: '配送员',
  finance: '财务'
}

// 加载用户列表数据
const loadData = async () => {
  loading.value = true
  try {
    // 调用接口，传搜索条件和分页参数
    const res = await getUserList({
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

// 点击搜索按钮
const handleSearch = () => {
  pagination.pageNum = 1  // 搜索后回到第一页
  loadData()
}

// 点击重置按钮
const handleReset = () => {
  // 清空所有搜索条件
  searchForm.username = ''
  searchForm.role = ''
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()  // 重新加载
}

// 点击新增用户按钮
const handleAdd = () => {
  isEdit.value = false  // 设为新增模式
  // 重置表单数据
  Object.assign(formData, {
    id: null,
    username: '',
    realName: '',
    role: '',
    phone: '',
    email: '',
    status: 1
  })
  dialogVisible.value = true  // 打开弹窗
}

// 点击编辑按钮
const handleEdit = (row) => {
  isEdit.value = true  // 设为编辑模式
  // 把当前行数据复制到表单里
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 点击删除按钮
const handleDelete = (row) => {
  // 弹确认框，防止误删
  ElMessageBox.confirm(`确定要删除用户 "${row.realName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteUser(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()  // 删除后刷新列表
    }
  }).catch(() => {})
}

// 重置密码按钮
const handleResetPwd = (row) => {
  // 重置后默认密码是123456，这个要告诉用户
  ElMessageBox.confirm(`确定要重置 "${row.realName}" 的密码吗？重置后密码为 123456`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await resetPassword(row.id)
    if (res.code === 200) {
      ElMessage.success(res.message)
    }
  }).catch(() => {})
}

// 提交表单（新增或编辑）
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 先校验表单
    await formRef.value.validate()
    submitLoading.value = true
    
    let res
    // 根据isEdit判断调用新增还是编辑接口
    if (isEdit.value) {
      res = await updateUser(formData.id, formData)
    } else {
      res = await addUser(formData)
    }
    
    // console.log('提交结果', res)  // 调试用，先注释掉
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()  // 刷新列表
    }
  } catch (e) {
    console.error('提交用户数据出错', e)  // 留一个console，方便调试
  } finally {
    submitLoading.value = false
  }
}

// 弹窗关闭后重置表单
const handleDialogClosed = () => {
  formRef.value?.resetFields()
}

// 页面加载时获取用户列表
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 搜索栏样式 - 顶部灰色背景的搜索区域 */
.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 工具栏样式 - 搜索下面放新增按钮的区域 */
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 分页包装器 - 让分页组件靠右显示 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
