<template>
  <!-- 整个库存管理页面的容器 -->
  <div class="page-container">
    <!-- 搜索栏区域 - 用来按条件筛选药品库存 -->
    <div class="search-bar">
      <!-- 搜索表单 - inline模式让表单项横向排列 -->
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <!-- 药品名称/编码输入框 -->
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.drugName" placeholder="请输入名称/编码" clearable style="width: 200px" />
        </el-form-item>
        <!-- 药品分类下拉选择 -->
        <el-form-item label="药品分类">
          <el-select v-model="searchForm.categoryName" placeholder="请选择分类" clearable style="width: 140px">
            <el-option label="抗生素类" value="抗生素类" />
            <el-option label="解热镇痛类" value="解热镇痛类" />
            <el-option label="消化系统类" value="消化系统类" />
            <el-option label="心血管系统类" value="心血管系统类" />
            <el-option label="呼吸系统类" value="呼吸系统类" />
            <el-option label="维生素及矿物质类" value="维生素及矿物质类" />
          </el-select>
        </el-form-item>
        <!-- 库存状态下拉选择 - 正常/预警 -->
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" value="normal" />
            <el-option label="库存预警" value="warning" />
          </el-select>
        </el-form-item>
        <!-- 搜索和重置按钮组 -->
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

    <!-- 操作工具栏 - 入库出库调拨按钮都在这里 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- 入库按钮 - 点击后提示在表格中选择药品 -->
        <el-button type="success" @click="handleStockIn">
          <el-icon><Plus /></el-icon>入库
        </el-button>
        <!-- 出库按钮 -->
        <el-button type="warning" @click="handleStockOut">
          <el-icon><Minus /></el-icon>出库
        </el-button>
        <!-- 调拨按钮 - 不同仓库之间调货 -->
        <el-button type="primary" @click="handleTransfer">
          <el-icon><Transfer /></el-icon>调拨
        </el-button>
      </div>
      <div class="toolbar-right">
        <!-- 库存预警徽章 - 显示有多少个药品库存不足了 -->
        <el-badge :value="warningCount" :max="99" class="warning-badge" type="danger">
          <el-button @click="showWarning = true">
            <el-icon><Warning /></el-icon>库存预警
          </el-button>
        </el-badge>
      </div>
    </div>

    <!-- 库存数据表格 - 展示所有药品库存信息 -->
    <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="drugCode" label="药品编码" width="120" />
      <el-table-column prop="drugName" label="药品名称" min-width="160" />
      <el-table-column prop="categoryName" label="分类" width="130" />
      <el-table-column prop="specification" label="规格" width="130" />
      <el-table-column prop="unit" label="单位" width="70" align="center" />
      <!-- 当前库存列 - 如果是预警状态就标红 -->
      <el-table-column prop="stock" label="当前库存" width="100" align="center">
        <template #default="{ row }">
          <span :class="{ 'warning-text': row.status === 'warning' }">{{ row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="minStock" label="最低库存" width="100" align="center" />
      <el-table-column prop="maxStock" label="最高库存" width="100" align="center" />
      <el-table-column prop="warehouse" label="仓库" width="100" />
      <el-table-column prop="location" label="货位" width="100" />
      <!-- 状态标签列 - 正常绿色 预警红色 -->
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 'normal' ? 'success' : 'danger'" size="small">
            {{ row.status === 'normal' ? '正常' : '预警' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 操作列 - 每行的入库出库调拨按钮 fixed固定在右边 -->
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="success" link size="small" @click="handleRowStockIn(row)">入库</el-button>
          <el-button type="warning" link size="small" @click="handleRowStockOut(row)">出库</el-button>
          <el-button type="primary" link size="small" @click="handleRowTransfer(row)">调拨</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 - 控制表格分页显示 -->
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

    <!-- 入库弹窗 - 点击行内入库按钮弹出 -->
    <el-dialog
      v-model="stockInDialogVisible"
      title="药品入库"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleStockInDialogClosed"
    >
      <!-- 入库表单 - 带验证规则 -->
      <el-form
        ref="stockInFormRef"
        :model="stockInForm"
        :rules="stockInRules"
        label-width="100px"
      >
        <el-form-item label="药品名称">
          <el-input v-model="stockInForm.drugName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input :value="stockInForm.currentStock + ' ' + stockInForm.unit" disabled />
        </el-form-item>
        <!-- 入库数量 - 用数字输入框，最少1 -->
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="stockInForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <!-- 入库类型下拉选择 -->
        <el-form-item label="入库类型" prop="type">
          <el-select v-model="stockInForm.type" placeholder="请选择入库类型" style="width: 100%">
            <el-option label="采购入库" value="采购入库" />
            <el-option label="调拨入库" value="调拨入库" />
            <el-option label="退货入库" value="退货入库" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="stockInForm.source" placeholder="请输入来源" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockInForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 - 取消和确认 -->
      <template #footer>
        <el-button @click="stockInDialogVisible = false">取消</el-button>
        <el-button type="success" :loading="stockInLoading" @click="handleStockInSubmit">确认入库</el-button>
      </template>
    </el-dialog>

    <!-- 出库弹窗 - 和入库弹窗结构差不多 -->
    <el-dialog
      v-model="stockOutDialogVisible"
      title="药品出库"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleStockOutDialogClosed"
    >
      <el-form
        ref="stockOutFormRef"
        :model="stockOutForm"
        :rules="stockOutRules"
        label-width="100px"
      >
        <el-form-item label="药品名称">
          <el-input v-model="stockOutForm.drugName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input :value="stockOutForm.currentStock + ' ' + stockOutForm.unit" disabled />
        </el-form-item>
        <!-- 出库数量 - 最大值不能超过当前库存 -->
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="stockOutForm.quantity" :min="1" :max="stockOutForm.currentStock" style="width: 100%" />
        </el-form-item>
        <!-- 出库类型 -->
        <el-form-item label="出库类型" prop="type">
          <el-select v-model="stockOutForm.type" placeholder="请选择出库类型" style="width: 100%">
            <el-option label="销售出库" value="销售出库" />
            <el-option label="调拨出库" value="调拨出库" />
            <el-option label="报损出库" value="报损出库" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="去向">
          <el-input v-model="stockOutForm.target" placeholder="请输入去向" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockOutForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockOutDialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="stockOutLoading" @click="handleStockOutSubmit">确认出库</el-button>
      </template>
    </el-dialog>

    <!-- 调拨弹窗 - 仓库之间调拨药品 -->
    <el-dialog
      v-model="transferDialogVisible"
      title="药品调拨"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleTransferDialogClosed"
    >
      <el-form
        ref="transferFormRef"
        :model="transferForm"
        :rules="transferRules"
        label-width="100px"
      >
        <el-form-item label="药品名称">
          <el-input v-model="transferForm.drugName" disabled />
        </el-form-item>
        <!-- 调出仓库 - 只读，从当前行带过来 -->
        <el-form-item label="调出仓库">
          <el-input v-model="transferForm.fromWarehouse" disabled />
        </el-form-item>
        <!-- 调入仓库 - 下拉选择目标仓库 -->
        <el-form-item label="调入仓库" prop="toWarehouse">
          <el-select v-model="transferForm.toWarehouse" placeholder="请选择调入仓库" style="width: 100%">
            <el-option label="第一乡镇卫生院仓库" value="第一乡镇卫生院仓库" />
            <el-option label="第二乡镇卫生院仓库" value="第二乡镇卫生院仓库" />
            <el-option label="第三乡镇卫生院仓库" value="第三乡镇卫生院仓库" />
          </el-select>
        </el-form-item>
        <!-- 调拨数量 - 不能超过当前库存 -->
        <el-form-item label="调拨数量" prop="quantity">
          <el-input-number v-model="transferForm.quantity" :min="1" :max="transferForm.currentStock" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="transferForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="transferLoading" @click="handleTransferSubmit">确认调拨</el-button>
      </template>
    </el-dialog>

    <!-- 库存预警列表弹窗 - 点击预警按钮弹出所有库存不足的药品 -->
    <el-dialog v-model="showWarning" title="库存预警列表" width="700px">
      <el-table :data="warningList" border stripe style="width: 100%">
        <el-table-column prop="drugName" label="药品名称" min-width="160" />
        <el-table-column prop="specification" label="规格" width="130" />
        <!-- 当前库存 - 红字加粗显示 -->
        <el-table-column prop="stock" label="当前库存" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="100" align="center" />
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <el-tag type="danger" size="small">库存不足</el-tag>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="showWarning = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 引入vue的组合式API - 这一块参考了vue3官方文档
import { ref, reactive, onMounted } from 'vue'
// 引入element-plus的消息提示组件
import { ElMessage } from 'element-plus'
// 引入库存相关的API接口
import { getInventoryList, stockIn, stockOut, stockTransfer, getWarningList } from '@/api/inventory'
// 引入用户状态管理，调拨时取当前用户的机构ID作为默认调入机构
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()

// 表格加载状态 - 用来显示loading动画
const loading = ref(false)
// 库存预警数量 - 显示在徽章上的数字
const warningCount = ref(0)
// 预警药品列表 - 存所有库存不足的药品
const warningList = ref([])
// 预警弹窗显示控制
const showWarning = ref(false)

// 搜索表单数据 - 绑定搜索栏的输入
const searchForm = reactive({
  drugName: '',       // 药品名称/编码
  categoryName: '',   // 药品分类
  status: '',         // 库存状态
  warehouse: ''       // 仓库 - 暂时没用上
})

// 分页参数 - 控制表格分页
const pagination = reactive({
  pageNum: 1,     // 当前页码
  pageSize: 10,   // 每页条数
  total: 0        // 总条数
})

// 表格数据 - 存库存列表
const tableData = ref([])

// ====== 入库相关变量 ======
// 入库弹窗是否显示
const stockInDialogVisible = ref(false)
// 入库提交按钮的loading状态
const stockInLoading = ref(false)
// 入库表单ref - 用来调用validate方法
const stockInFormRef = ref(null)
// 入库表单数据
// drugId / institutionId 是后端 StockOperationDTO 要求的必填字段，
// 之前漏掉这俩导致后端 DTO 全 null，直接进 GlobalExceptionHandler 兜底成"系统异常"
const stockInForm = reactive({
  id: null,              // 库存记录id
  drugId: null,          // 药品ID（后端必填）
  institutionId: null,   // 机构ID（后端必填，这条库存所属机构）
  drugName: '',          // 药品名称（前端展示用）
  currentStock: 0,       // 当前库存（前端展示用）
  unit: '',              // 单位（前端展示用）
  quantity: 1,           // 入库数量，默认1
  type: '采购入库',      // 入库类型，默认采购入库
  source: '',            // 来源
  remark: ''             // 备注
})
// 入库表单验证规则 - 这块参考了element-plus官方文档改的
const stockInRules = {
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  type: [{ required: true, message: '请选择入库类型', trigger: 'change' }]
}

// ====== 出库相关变量 ======
const stockOutDialogVisible = ref(false)
const stockOutLoading = ref(false)
const stockOutFormRef = ref(null)
// 出库表单数据
// drugId / institutionId 是后端 StockOperationDTO 要求的必填字段，
// 之前漏掉这俩导致后端查不到库存直接抛 BusinessException
const stockOutForm = reactive({
  id: null,
  drugId: null,          // 药品ID（后端必填）
  institutionId: null,   // 机构ID（后端必填，这条库存所属机构）
  drugName: '',
  currentStock: 0,
  unit: '',
  quantity: 1,
  type: '销售出库',      // 出库类型默认销售出库
  target: '',            // 去向
  remark: ''
})
// 出库表单验证规则
const stockOutRules = {
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }],
  type: [{ required: true, message: '请选择出库类型', trigger: 'change' }]
}

// ====== 调拨相关变量 ======
const transferDialogVisible = ref(false)
const transferLoading = ref(false)
const transferFormRef = ref(null)
// 调拨表单数据
// drugId / fromInstitutionId / toInstitutionId 是后端 StockOperationDTO 要求的必填字段，
// 之前全是 undefined，后端 transfer 逻辑根本跑不通
const transferForm = reactive({
  id: null,
  drugId: null,                // 药品ID（后端必填）
  fromInstitutionId: null,     // 调出机构ID（= row.institutionId，这条库存所属机构）
  toInstitutionId: null,       // 调入机构ID（= 当前登录用户所属机构）
  drugName: '',
  fromWarehouse: '中心仓库',   // 调出仓库（前端展示用）
  toWarehouse: '',             // 调入仓库（前端展示用，暂未映射到 toInstitutionId）
  currentStock: 0,
  quantity: 1,
  remark: ''
})
// 调拨表单验证规则
const transferRules = {
  toWarehouse: [{ required: true, message: '请选择调入仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入调拨数量', trigger: 'blur' }]
}

// 加载库存列表数据 - 页面初始化和搜索分页都调这个
const loadData = async () => {
  loading.value = true
  try {
    // 调用后端接口获取库存列表
    const res = await getInventoryList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    // console.log('库存列表返回数据：', res)  // 调试用，上线前删掉
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 加载预警数量 - 获取所有库存不足的药品
const loadWarningCount = async () => {
  const res = await getWarningList()
  if (res.code === 200) {
    warningList.value = res.data
    warningCount.value = res.data.length
  }
}

// 搜索按钮点击事件 - 重置页码为1然后重新加载
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置按钮点击事件 - 清空搜索条件重新加载
const handleReset = () => {
  searchForm.drugName = ''
  searchForm.categoryName = ''
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

// 顶部入库按钮点击 - 提示用户从表格行操作
// TODO: 以后可以改成弹出药品选择框，不用非得点行按钮
const handleStockIn = () => {
  ElMessage.info('请在表格中选择需要入库的药品')
}

// 顶部出库按钮点击
const handleStockOut = () => {
  ElMessage.info('请在表格中选择需要出库的药品')
}

// 顶部调拨按钮点击
const handleTransfer = () => {
  ElMessage.info('请在表格中选择需要调拨的药品')
}

// 行内入库按钮 - 把当前行数据填到入库表单里然后弹窗
// 关键修复：把 row 对象里的 drugId 和 institutionId 也塞进去，
// 不然 API 层发给后端的 StockOperationDTO 全 null，后端会抛"系统异常"
const handleRowStockIn = (row) => {
  Object.assign(stockInForm, {
    id: row.id,
    drugId: row.drugId,           // 后端必填
    institutionId: row.institutionId, // 后端必填
    drugName: row.drugName,
    currentStock: row.stock,
    unit: row.unit,
    quantity: 1,
    type: '采购入库',
    source: '',
    remark: ''
  })
  stockInDialogVisible.value = true
}

// 行内出库按钮 - 类似入库
const handleRowStockOut = (row) => {
  Object.assign(stockOutForm, {
    id: row.id,
    drugId: row.drugId,           // 后端必填
    institutionId: row.institutionId, // 后端必填
    drugName: row.drugName,
    currentStock: row.stock,
    unit: row.unit,
    quantity: 1,
    type: '销售出库',
    target: '',
    remark: ''
  })
  stockOutDialogVisible.value = true
}

// 行内调拨按钮
// 关键修复：drugId、fromInstitutionId、toInstitutionId 全要塞进去
// fromInstitutionId = row.institutionId（这条库存所属机构就是调出机构）
// toInstitutionId = 当前登录用户所属机构（简化策略，暂未做仓库名→机构ID映射）
const handleRowTransfer = (row) => {
  Object.assign(transferForm, {
    id: row.id,
    drugId: row.drugId,
    fromInstitutionId: row.institutionId,
    toInstitutionId: userStore.userInfo?.institutionId ?? null,
    drugName: row.drugName,
    fromWarehouse: row.warehouse,
    toWarehouse: '',
    currentStock: row.stock,
    quantity: 1,
    remark: ''
  })
  transferDialogVisible.value = true
}

// 入库提交 - 验证表单后调接口
const handleStockInSubmit = async () => {
  if (!stockInFormRef.value) return
  try {
    // 表单验证
    await stockInFormRef.value.validate()
    stockInLoading.value = true
    const res = await stockIn(stockInForm)
    // console.log('入库返回：', res) // 调试用
    if (res.code === 200) {
      ElMessage.success('入库成功')
      stockInDialogVisible.value = false
      // 重新加载列表和预警数
      loadData()
      loadWarningCount()
    }
  } catch (e) {
    console.error(e)
  } finally {
    stockInLoading.value = false
  }
}

// 出库提交
const handleStockOutSubmit = async () => {
  if (!stockOutFormRef.value) return
  try {
    await stockOutFormRef.value.validate()
    stockOutLoading.value = true
    const res = await stockOut(stockOutForm)
    if (res.code === 200) {
      ElMessage.success('出库成功')
      stockOutDialogVisible.value = false
      loadData()
      loadWarningCount()
    }
  } catch (e) {
    console.error(e)
  } finally {
    stockOutLoading.value = false
  }
}

// 调拨提交
const handleTransferSubmit = async () => {
  if (!transferFormRef.value) return
  try {
    await transferFormRef.value.validate()
    transferLoading.value = true
    const res = await stockTransfer(transferForm)
    if (res.code === 200) {
      ElMessage.success('调拨成功')
      transferDialogVisible.value = false
      loadData()
    }
  } catch (e) {
    console.error(e)
  } finally {
    transferLoading.value = false
  }
}

// 入库弹窗关闭回调 - 重置表单
const handleStockInDialogClosed = () => {
  stockInFormRef.value?.resetFields()
}

// 出库弹窗关闭回调
const handleStockOutDialogClosed = () => {
  stockOutFormRef.value?.resetFields()
}

// 调拨弹窗关闭回调
const handleTransferDialogClosed = () => {
  transferFormRef.value?.resetFields()
}

// 页面挂载完成后加载数据
onMounted(() => {
  loadData()
  loadWarningCount()
})

// TODO: 后面再加个导出功能，把库存列表导出成Excel
// TODO: 库存流水记录还没做，等后端接口
</script>

<style scoped>
/* 搜索栏样式 - 灰色背景圆角 */
.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 工具栏样式 - 左右分布 */
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 预警文字样式 - 红色加粗 */
.warning-text {
  color: #f56c6c;
  font-weight: bold;
}

/* 分页容器 - 右对齐 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 预警徽章 - 去掉右边距 */
.warning-badge {
  margin-right: 0;
}
</style>
