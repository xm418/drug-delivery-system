<template>
  <!-- 订单管理页面容器 -->
  <div class="page-container">
    <!-- 搜索栏区域 - 上面那一排搜索条件 -->
    <div class="search-bar">
      <!-- 搜索表单，inline模式就是横向排列 -->
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <!-- 订单号输入框 -->
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 180px" />
        </el-form-item>
        <!-- 医疗机构名称输入框 -->
        <el-form-item label="医疗机构">
          <el-input v-model="searchForm.institutionName" placeholder="请输入机构名称" clearable style="width: 180px" />
        </el-form-item>
        <!-- 订单状态下拉选择 -->
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option label="待审核" value="pending" />
            <el-option label="已审核" value="approved" />
            <el-option label="配送中" value="delivering" />
            <el-option label="已完成" value="completed" />
            <el-option label="已驳回" value="rejected" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <!-- 下单时间范围选择器 -->
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
            value-format="YYYY-MM-DD"
          />
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

    <!-- 操作栏 - 搜索下面那一排，放创建订单按钮 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- 创建订单按钮，点了弹对话框 -->
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>创建订单
        </el-button>
      </div>
    </div>

    <!-- 订单列表表格 - 主要内容区域 -->
    <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
      <!-- 序号列 -->
      <el-table-column type="index" label="序号" width="60" align="center" />
      <!-- 订单号列 -->
      <el-table-column prop="orderNo" label="订单号" width="170" />
      <!-- 医疗机构名称，太长会显示省略号 -->
      <el-table-column prop="institutionName" label="医疗机构" min-width="160" />
      <!-- 药品数量 -->
      <el-table-column prop="totalQuantity" label="药品数量" width="100" align="center" />
      <!-- 订单金额，右对齐显示人民币 -->
      <el-table-column prop="totalAmount" label="订单金额(元)" width="130" align="right">
        <template #default="{ row }">
          ¥{{ row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <!-- 状态列，不同状态不同颜色 -->
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">{{ row.statusName }}</el-tag>
        </template>
      </el-table-column>
      <!-- 下单人 -->
      <el-table-column prop="createBy" label="下单人" width="100" />
      <!-- 下单时间 -->
      <el-table-column prop="createTime" label="下单时间" min-width="170" />
      <!-- 操作列，固定在右边，里面有详情、审核通过、驳回、取消按钮 -->
      <!-- TODO: 可以加个批量审核的功能，一个个点有点麻烦 -->
      <el-table-column label="操作" width="240" fixed="right" align="center">
        <template #default="{ row }">
          <!-- 详情按钮，所有人都能看 -->
          <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
          <!-- 审核通过按钮，只有待审核状态才显示 -->
          <el-button
            v-if="row.status === 'pending'"
            type="success"
            link
            size="small"
            @click="handleAudit(row, 'approved')"
          >
            审核通过
          </el-button>
          <!-- 驳回按钮，待审核状态才显示 -->
          <el-button
            v-if="row.status === 'pending'"
            type="danger"
            link
            size="small"
            @click="handleAudit(row, 'rejected')"
          >
            驳回
          </el-button>
          <!-- 取消按钮，待审核状态才显示 -->
          <el-button
            v-if="row.status === 'pending'"
            type="info"
            link
            size="small"
            @click="handleCancel(row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 - 表格下面的分页 -->
    <!-- 这里是我自己写的分页逻辑，用element-plus的el-pagination组件 -->
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

    <!-- 创建订单弹窗 - 点创建订单弹出来的对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建订单"
      width="800px"
      :close-on-click-modal="false"
      @closed="handleCreateDialogClosed"
    >
      <!-- 创建订单的表单 -->
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <!-- 第一行：医疗机构和备注，左右布局 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="医疗机构" prop="institutionId">
              <!-- 医疗机构下拉选择 -->
              <!-- TODO: 后面改成从接口动态获取，现在先写死几个 -->
              <el-select v-model="createForm.institutionId" placeholder="请选择医疗机构" style="width: 100%">
                <el-option label="第一乡镇卫生院" :value="11" />
                <el-option label="第二乡镇卫生院" :value="12" />
                <el-option label="第三乡镇卫生院" :value="21" />
                <el-option label="东村村卫生室" :value="111" />
                <el-option label="西村村卫生室" :value="112" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="createForm.remark" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 药品明细部分 - 下面的药品列表 -->
        <el-form-item label="药品明细">
          <div class="drug-list-section">
            <!-- 添加药品按钮 -->
            <el-button type="primary" size="small" @click="handleAddDrug">
              <el-icon><Plus /></el-icon>添加药品
            </el-button>
            <!-- 药品明细表格，可以加多个药品 -->
            <el-table :data="createForm.items" border style="width: 100%; margin-top: 10px" size="small">
              <!-- 药品名称列，下拉选择 -->
              <el-table-column prop="drugName" label="药品名称" min-width="150">
                <template #default="{ row, $index }">
                  <el-select v-model="row.drugId" placeholder="请选择药品" style="width: 100%" @change="handleDrugChange($index)">
                    <el-option
                      v-for="drug in drugOptions"
                      :key="drug.id"
                      :label="drug.name + ' (' + drug.specification + ')'"
                      :value="drug.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <!-- 规格列 -->
              <el-table-column prop="specification" label="规格" width="120" />
              <!-- 单位列 -->
              <el-table-column prop="unit" label="单位" width="70" align="center" />
              <!-- 单价列，右对齐 -->
              <el-table-column prop="price" label="单价" width="100" align="right">
                <template #default="{ row }">
                  {{ row.price ? row.price.toFixed(2) : '0.00' }}
                </template>
              </el-table-column>
              <!-- 数量列，数字输入框 -->
              <el-table-column prop="quantity" label="数量" width="120">
                <template #default="{ row }">
                  <el-input-number v-model="row.quantity" :min="1" size="small" style="width: 100%" @change="calculateTotal" />
                </template>
              </el-table-column>
              <!-- 小计金额 = 单价 * 数量 -->
              <el-table-column prop="amount" label="小计" width="100" align="right">
                <template #default="{ row }">
                  {{ (row.price * row.quantity).toFixed(2) }}
                </template>
              </el-table-column>
              <!-- 删除按钮，移除该行药品 -->
              <el-table-column label="操作" width="70" align="center">
                <template #default="{ $index }">
                  <el-button type="danger" link size="small" @click="handleRemoveDrug($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 合计金额显示 -->
            <div class="total-amount">
              <span>合计：</span>
              <span class="amount">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="handleCreateSubmit">提交订单</el-button>
      </template>
    </el-dialog>

    <!-- 审核驳回弹窗 - 点驳回弹出来的，要填驳回原因 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回订单" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectForm.remark" type="textarea" :rows="4" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditLoading" @click="handleRejectSubmit">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 引入vue相关API
import { ref, reactive, computed, onMounted } from 'vue'
// 路由，用来跳转到详情页
import { useRouter } from 'vue-router'
// element-plus的消息提示和确认框
import { ElMessage, ElMessageBox } from 'element-plus'
// 订单相关接口
import { getOrderList, createOrder, auditOrder, cancelOrder } from '@/api/order'
// 药品列表接口，创建订单时选药品用
import { getDrugList } from '@/api/drug'

// 路由实例
const router = useRouter()
// 表格加载状态
const loading = ref(false)
// 创建订单提交时的loading
const createLoading = ref(false)
// 审核操作的loading
const auditLoading = ref(false)
// 创建订单弹窗显示状态
const createDialogVisible = ref(false)
// 驳回弹窗显示状态
const rejectDialogVisible = ref(false)
// 创建订单表单的ref引用
const createFormRef = ref(null)

// 日期范围选择器的值
const dateRange = ref([])
// 药品下拉选项列表
const drugOptions = ref([])

// 搜索表单数据 - 上面那一排搜索条件
const searchForm = reactive({
  orderNo: '',           // 订单号
  institutionName: '',   // 医疗机构名称
  status: '',            // 订单状态
  startTime: '',         // 开始时间
  endTime: ''            // 结束时间
})

// 分页数据 - 表格分页用
const pagination = reactive({
  pageNum: 1,     // 当前第几页
  pageSize: 10,   // 每页多少条
  total: 0        // 总共多少条
})

// 表格数据
const tableData = ref([])

// 创建订单的表单数据
const createForm = reactive({
  institutionId: '',     // 医疗机构ID
  institutionName: '',   // 医疗机构名称
  remark: '',            // 备注
  items: []              // 药品明细列表
})

// 创建订单的表单校验规则
const createRules = {
  institutionId: [{ required: true, message: '请选择医疗机构', trigger: 'change' }]
}

// 驳回表单数据
const rejectForm = reactive({
  id: '',       // 订单ID
  remark: ''    // 驳回原因
})

// 计算属性：订单总金额 - 把所有药品的单价*数量加起来
const totalAmount = computed(() => {
  return createForm.items.reduce((sum, item) => sum + (item.price * item.quantity || 0), 0)
})

// 根据订单状态返回对应的tag颜色类型
// 跟dashboard页面那个一样，后面可以抽出去
const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    approved: 'primary',
    delivering: 'success',
    completed: 'info',
    rejected: 'danger',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

// 加载订单列表数据
const loadData = async () => {
  loading.value = true
  try {
    // 组装请求参数
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    // 如果选了日期范围，加上开始和结束时间
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1] + ' 23:59:59'  // 结束时间加一天的最后一秒
    }
    const res = await getOrderList(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 加载药品列表 - 创建订单时下拉选择用
const loadDrugs = async () => {
  // 先加载100条，应该够用了
  const res = await getDrugList({ pageNum: 1, pageSize: 100 })
  if (res.code === 200) {
    drugOptions.value = res.data.list
  }
}

// 点击搜索按钮
const handleSearch = () => {
  pagination.pageNum = 1  // 搜索的时候重置到第一页
  loadData()
}

// 点击重置按钮
const handleReset = () => {
  // 把搜索条件都清空
  searchForm.orderNo = ''
  searchForm.institutionName = ''
  searchForm.status = ''
  dateRange.value = []
  pagination.pageNum = 1
  loadData()  // 重新加载数据
}

// 点击详情按钮，跳转到订单详情页
const handleDetail = (row) => {
  router.push(`/order/detail/${row.id}`)
}

// 点击创建订单按钮，打开弹窗
const handleCreate = () => {
  // 先把表单数据清空
  createForm.institutionId = ''
  createForm.institutionName = ''
  createForm.remark = ''
  createForm.items = []
  createDialogVisible.value = true
}

// 添加药品行 - 在创建订单弹窗里点添加药品按钮
const handleAddDrug = () => {
  createForm.items.push({
    drugId: '',
    drugName: '',
    specification: '',
    unit: '',
    price: 0,
    quantity: 1,  // 默认数量是1
    amount: 0
  })
}

// 选择药品后触发 - 自动填充规格、单位、单价等信息
const handleDrugChange = (index) => {
  const item = createForm.items[index]
  // 从药品列表里找到选中的那个药品
  const drug = drugOptions.value.find(d => d.id === item.drugId)
  if (drug) {
    item.drugName = drug.name
    item.specification = drug.specification
    item.unit = drug.unit
    item.price = drug.price
  }
  calculateTotal()  // 重新计算总价（其实计算属性会自动更新，这里留着是为了以后扩展）
}

// 删除药品行
const handleRemoveDrug = (index) => {
  createForm.items.splice(index, 1)
}

// 计算总金额 - 其实计算属性会自动算，这个函数暂时没用
// 不过留着吧，以后可能有其他逻辑要加
const calculateTotal = () => {
  // 触发计算属性更新
}

// 提交创建订单
const handleCreateSubmit = async () => {
  if (!createFormRef.value) return
  
  // 校验：至少得有一种药品吧
  if (createForm.items.length === 0) {
    ElMessage.warning('请至少添加一种药品')
    return
  }
  
  // 校验：每一行药品都得选药品并且数量大于0
  const hasInvalidItem = createForm.items.some(item => !item.drugId || item.quantity <= 0)
  if (hasInvalidItem) {
    ElMessage.warning('请完善所有药品信息')
    return
  }
  
  try {
    // 先校验表单
    await createFormRef.value.validate()
    createLoading.value = true
    
    const totalAmountVal = totalAmount.value
    // 计算总数量
    const totalQuantity = createForm.items.reduce((sum, item) => sum + item.quantity, 0)
    
    // 医疗机构ID和名称的映射
    // TODO: 后面应该从接口获取医疗机构列表，不用写死
    const institutionMap = {
      11: '第一乡镇卫生院',
      12: '第二乡镇卫生院',
      21: '第三乡镇卫生院',
      111: '东村村卫生室',
      112: '西村村卫生室'
    }
    
    // 调用创建订单接口
    const res = await createOrder({
      institutionId: createForm.institutionId,
      institutionName: institutionMap[createForm.institutionId] || '',
      totalAmount: totalAmountVal,
      totalQuantity,
      remark: createForm.remark,
      items: createForm.items
    })
    
    if (res.code === 200) {
      ElMessage.success('订单创建成功')
      createDialogVisible.value = false
      loadData()  // 刷新列表
    }
  } catch (e) {
    console.error('创建订单出错了', e)  // 留一个console，方便排查问题
  } finally {
    createLoading.value = false
  }
}

// 审核操作 - 通过或者驳回
const handleAudit = (row, status) => {
  if (status === 'approved') {
    // 通过的话直接弹确认框
    ElMessageBox.confirm(`确定要通过订单 "${row.orderNo}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }).then(async () => {
      auditLoading.value = true
      const res = await auditOrder(row.id, { status: 'approved' })
      auditLoading.value = false
      if (res.code === 200) {
        ElMessage.success('审核通过')
        loadData()
      }
    }).catch(() => {})
  } else {
    // 驳回的话打开弹窗填原因
    rejectForm.id = row.id
    rejectForm.remark = ''
    rejectDialogVisible.value = true
  }
}

// 提交驳回
const handleRejectSubmit = async () => {
  // 驳回原因不能为空
  if (!rejectForm.remark) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  auditLoading.value = true
  const res = await auditOrder(rejectForm.id, { status: 'rejected', remark: rejectForm.remark })
  auditLoading.value = false
  if (res.code === 200) {
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadData()
  }
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消订单 "${row.orderNo}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await cancelOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadData()
    }
  }).catch(() => {})
}

// 创建订单弹窗关闭后重置表单
const handleCreateDialogClosed = () => {
  createFormRef.value?.resetFields()
}

// 页面挂载后加载数据
onMounted(() => {
  loadData()     // 加载订单列表
  loadDrugs()    // 加载药品列表（创建订单用）
})
</script>

<style scoped>
/* 搜索栏样式 - 上面那排灰色背景的搜索区域 */
.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 工具栏样式 - 搜索下面放按钮的那行 */
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 分页包装器 - 让分页靠右显示 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 药品列表区域 - 创建订单弹窗里的药品表格那块 */
.drug-list-section {
  width: 100%;
}

/* 合计金额行 - 药品表格下面的总价显示 */
.total-amount {
  margin-top: 12px;
  text-align: right;
  font-size: 16px;
}

/* 合计金额数字 - 红色加粗的那个钱数 */
.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 20px;
  margin-left: 8px;
}
</style>
