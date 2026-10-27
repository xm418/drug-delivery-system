<template>
  <!-- 配送任务管理页面容器 -->
  <div class="page-container">
    <!-- 搜索栏 - 按任务编号、机构、状态、配送员筛选 -->
    <div class="search-bar">
      <!-- 内联搜索表单 -->
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <!-- 任务编号输入框 -->
        <el-form-item label="任务编号">
          <el-input v-model="searchForm.taskNo" placeholder="请输入任务编号" clearable style="width: 180px" />
        </el-form-item>
        <!-- 医疗机构名称输入框 -->
        <el-form-item label="医疗机构">
          <el-input v-model="searchForm.institutionName" placeholder="请输入机构名称" clearable style="width: 180px" />
        </el-form-item>
        <!-- 任务状态下拉选择 -->
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待配送" value="pending" />
            <el-option label="配送中" value="delivering" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <!-- 配送员姓名输入框 -->
        <el-form-item label="配送员">
          <el-input v-model="searchForm.driver" placeholder="请输入配送员姓名" clearable style="width: 150px" />
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

    <!-- 操作工具栏 - 创建任务按钮和视图切换 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- 创建配送任务按钮 -->
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>创建任务
        </el-button>
      </div>
      <div class="toolbar-right">
        <!-- 视图切换单选按钮组 - 列表/卡片两种视图 -->
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="list">列表视图</el-radio-button>
          <el-radio-button value="card">卡片视图</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 列表视图 - 默认显示的表格形式 -->
    <div v-if="viewMode === 'list'">
      <!-- 配送任务表格 -->
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="taskNo" label="任务编号" width="170" />
        <el-table-column prop="orderNo" label="关联订单" width="170" />
        <el-table-column prop="institutionName" label="收货机构" min-width="160" />
        <el-table-column prop="receiver" label="收货人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <!-- 配送员列 - 没有的话显示横杠 -->
        <el-table-column prop="driver" label="配送员" width="100">
          <template #default="{ row }">
            {{ row.driver || '-' }}
          </template>
        </el-table-column>
        <!-- 状态标签列 - 不同状态不同颜色 -->
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column prop="estimatedTime" label="预计送达" min-width="170" />
        <!-- 操作列 - 根据状态显示不同按钮 -->
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <!-- 待配送且未分配配送员时显示分配按钮 -->
            <el-button
              v-if="row.status === 'pending' && !row.driver"
              type="success"
              link
              size="small"
              @click="handleAssign(row)"
            >
              分配配送员
            </el-button>
            <!-- 待配送且已分配配送员时显示开始配送按钮 -->
            <el-button
              v-if="row.status === 'pending' && row.driver"
              type="warning"
              link
              size="small"
              @click="handleStart(row)"
            >
              开始配送
            </el-button>
            <!-- 配送中显示确认送达按钮 -->
            <el-button
              v-if="row.status === 'delivering'"
              type="success"
              link
              size="small"
              @click="handleComplete(row)"
            >
              确认送达
            </el-button>
            <!-- 待配送状态可以取消 -->
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              link
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 卡片视图 - 另一种展示方式，像卡片一样排列 -->
    <div v-else class="card-view">
      <!-- 响应式栅格布局 - 不同屏幕宽度显示不同列数 -->
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.id">
          <!-- 任务卡片 - 带hover阴影效果 -->
          <el-card class="task-card" shadow="hover">
            <!-- 卡片头部 - 任务编号和状态标签 -->
            <div class="card-header">
              <span class="task-no">{{ item.taskNo }}</span>
              <el-tag :type="getStatusType(item.status)" size="small">{{ item.statusName }}</el-tag>
            </div>
            <!-- 卡片主体 - 收货信息 -->
            <div class="card-body">
              <div class="info-row">
                <span class="label">收货机构：</span>
                <span class="value">{{ item.institutionName }}</span>
              </div>
              <div class="info-row">
                <span class="label">收货人：</span>
                <span class="value">{{ item.receiver }}</span>
              </div>
              <div class="info-row">
                <span class="label">联系电话：</span>
                <span class="value">{{ item.phone }}</span>
              </div>
              <div class="info-row">
                <span class="label">配送员：</span>
                <span class="value">{{ item.driver || '未分配' }}</span>
              </div>
              <div class="info-row">
                <span class="label">预计送达：</span>
                <span class="value">{{ item.estimatedTime }}</span>
              </div>
            </div>
            <!-- 卡片底部 - 操作按钮 -->
            <div class="card-footer">
              <el-button type="primary" link size="small" @click="handleDetail(item)">详情</el-button>
              <el-button
                v-if="item.status === 'pending' && !item.driver"
                type="success"
                link
                size="small"
                @click="handleAssign(item)"
              >
                分配配送员
              </el-button>
              <el-button
                v-if="item.status === 'pending' && item.driver"
                type="warning"
                link
                size="small"
                @click="handleStart(item)"
              >
                开始配送
              </el-button>
              <el-button
                v-if="item.status === 'delivering'"
                type="success"
                link
                size="small"
                @click="handleComplete(item)"
              >
                确认送达
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

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

    <!-- 分配配送员弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="分配配送员" width="450px">
      <!-- 分配表单 -->
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="任务编号">
          <el-input v-model="assignForm.taskNo" disabled />
        </el-form-item>
        <!-- 配送员下拉选择 -->
        <el-form-item label="配送员" prop="driver">
          <el-select v-model="assignForm.driver" placeholder="请选择配送员" style="width: 100%">
            <el-option label="刘师傅" value="刘师傅" />
            <el-option label="张师傅" value="张师傅" />
            <el-option label="王师傅" value="王师傅" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="assignForm.driverPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情弹窗 - 用描述列表展示详细信息 -->
    <el-dialog v-model="detailDialogVisible" title="任务详情" width="650px">
      <!-- 描述列表组件 - 两列布局 -->
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="任务编号">{{ currentTask.taskNo }}</el-descriptions-item>
        <el-descriptions-item label="关联订单">{{ currentTask.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="收货机构">{{ currentTask.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentTask.receiver }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentTask.phone }}</el-descriptions-item>
        <!-- 地址跨两列 -->
        <el-descriptions-item label="地址" :span="2">{{ currentTask.address }}</el-descriptions-item>
        <el-descriptions-item label="配送员">{{ currentTask.driver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="配送员电话">{{ currentTask.driverPhone || '-' }}</el-descriptions-item>
        <!-- 任务状态带标签 -->
        <el-descriptions-item label="任务状态">
          <el-tag :type="getStatusType(currentTask.status)" size="small">{{ currentTask.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask.createTime }}</el-descriptions-item>
        <el-descriptions-item label="开始配送时间">{{ currentTask.startTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="送达时间">{{ currentTask.endTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预计送达">{{ currentTask.estimatedTime }}</el-descriptions-item>
        <el-descriptions-item label="药品数量">{{ currentTask.totalQuantity }}</el-descriptions-item>
        <!-- 金额保留两位小数 -->
        <el-descriptions-item label="订单金额">¥{{ currentTask.totalAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentTask.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 引入vue组合式API
import { ref, reactive, onMounted } from 'vue'
// 引入element-plus的消息和确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入配送相关API接口
import { getDeliveryTaskList, updateTaskStatus, assignDriver, createDeliveryTask } from '@/api/delivery'

// 表格加载状态
const loading = ref(false)
// 提交按钮loading状态
const submitLoading = ref(false)
// 视图模式 - list列表 / card卡片，默认列表
const viewMode = ref('list')
// 分配配送员弹窗显示控制
const assignDialogVisible = ref(false)
// 详情弹窗显示控制
const detailDialogVisible = ref(false)
// 当前查看的任务数据
const currentTask = ref(null)

// 搜索表单数据
const searchForm = reactive({
  taskNo: '',             // 任务编号
  institutionName: '',    // 机构名称
  status: '',             // 任务状态
  driver: ''              // 配送员
})

// 分页参数
const pagination = reactive({
  pageNum: 1,     // 当前页
  pageSize: 10,   // 每页大小
  total: 0        // 总记录数
})

// 表格数据 - 配送任务列表
const tableData = ref([])

// 分配配送员表单数据
const assignForm = reactive({
  id: '',             // 任务id
  taskNo: '',         // 任务编号
  driver: '',         // 配送员姓名
  driverPhone: ''     // 配送员电话
})

// 根据状态值返回对应的tag类型 - 这块参考了element-plus的tag文档写的
const getStatusType = (status) => {
  const map = {
    pending: 'warning',      // 待配送 - 警告黄
    delivering: 'primary',   // 配送中 - 主题蓝
    completed: 'success',    // 已完成 - 成功绿
    cancelled: 'info'        // 已取消 - 信息灰
  }
  return map[status] || 'info'
}

// 加载配送任务列表数据
const loadData = async () => {
  loading.value = true
  try {
    // 调用接口，传搜索条件和分页参数
    const res = await getDeliveryTaskList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    // console.log('配送任务列表：', res.data)  // 调试用，看看返回的数据对不对
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 搜索按钮 - 重置页码再加载
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置按钮 - 清空所有搜索条件
const handleReset = () => {
  searchForm.taskNo = ''
  searchForm.institutionName = ''
  searchForm.status = ''
  searchForm.driver = ''
  pagination.pageNum = 1
  loadData()
}

// 创建任务按钮点击 - 提示从订单详情页发起
// TODO: 后面可以直接在这里加个创建表单，不用跳来跳去的
const handleCreate = () => {
  ElMessage.info('创建任务功能可从订单详情页发起')
}

// 查看详情 - 把当前行数据存起来然后弹窗
const handleDetail = (row) => {
  currentTask.value = row
  detailDialogVisible.value = true
}

// 分配配送员按钮 - 初始化表单数据
const handleAssign = (row) => {
  assignForm.id = row.id
  assignForm.taskNo = row.taskNo
  assignForm.driver = ''
  assignForm.driverPhone = ''
  assignDialogVisible.value = true
}

// 分配配送员提交 - 调接口分配
const handleAssignSubmit = async () => {
  // 简单校验一下 - 没选配送员就提示
  if (!assignForm.driver) {
    ElMessage.warning('请选择配送员')
    return
  }
  submitLoading.value = true
  const res = await assignDriver(assignForm.id, assignForm)
  submitLoading.value = false
  if (res.code === 200) {
    ElMessage.success('配送员分配成功')
    assignDialogVisible.value = false
    loadData()  // 重新加载列表
  }
}

// 开始配送 - 弹确认框然后改状态
const handleStart = (row) => {
  ElMessageBox.confirm(`确定要开始配送任务 "${row.taskNo}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await updateTaskStatus(row.id, 'delivering')
    if (res.code === 200) {
      ElMessage.success('已开始配送')
      loadData()
    }
  }).catch(() => {})  // 用户点取消就啥也不干
}

// 确认送达 - 类似开始配送
const handleComplete = (row) => {
  ElMessageBox.confirm(`确定任务 "${row.taskNo}" 已送达吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    const res = await updateTaskStatus(row.id, 'completed')
    // console.log('确认送达返回：', res) // 调试用
    if (res.code === 200) {
      ElMessage.success('配送完成')
      loadData()
    }
  }).catch(() => {})
}

// 取消任务
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消任务 "${row.taskNo}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await updateTaskStatus(row.id, 'cancelled')
    if (res.code === 200) {
      ElMessage.success('任务已取消')
      loadData()
    }
  }).catch(() => {})
}

// 页面挂载后加载数据
onMounted(() => {
  loadData()
})

// TODO: 后面加个配送轨迹地图显示功能，用高德或者百度地图
// TODO: 批量分配配送员功能还没做
</script>

<style scoped>
/* 搜索栏样式 */
.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 工具栏样式 - 左右两端对齐 */
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

/* 卡片视图容器 */
.card-view {
  margin-bottom: 16px;
}

/* 任务卡片样式 - 底部留间距 */
.task-card {
  margin-bottom: 16px;
}

/* 卡片头部 - 左右分布，底部分隔线 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

/* 任务编号文字 - 加粗 */
.task-no {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

/* 卡片主体信息行 */
.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 13px;
}

/* 信息标签 - 灰色，固定宽度 */
.label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

/* 信息值 - 深灰色，超出省略号 */
.value {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 卡片底部 - 顶部分隔线，按钮靠右 */
.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
