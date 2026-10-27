<template>
  <div class="page-container">
    <div class="detail-header">
      <el-page-header @back="goBack" content="订单详情" />
    </div>

    <div v-loading="loading" class="detail-content">
      <!-- 订单基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">订单信息</span>
            <el-tag :type="getStatusType(orderInfo.status)" size="large">{{ orderInfo.statusName }}</el-tag>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="医疗机构">{{ orderInfo.institutionName }}</el-descriptions-item>
          <el-descriptions-item label="下单人">{{ orderInfo.createBy }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderInfo.createTime }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ orderInfo.auditBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ orderInfo.auditTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="药品总数">{{ orderInfo.totalQuantity || 0 }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span style="color: #f56c6c; font-weight: bold">¥{{ (orderInfo.totalAmount || 0).toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ orderInfo.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 收货信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">收货信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="收货机构">{{ orderInfo.institutionName }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ receiverInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ receiverInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ receiverInfo.address }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 药品明细 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">药品明细</span>
            <span class="item-count">共 {{ orderInfo.items?.length || 0 }} 种药品</span>
          </div>
        </template>
        <el-table :data="orderInfo.items || []" border stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="drugName" label="药品名称" min-width="180" />
          <el-table-column prop="specification" label="规格" width="140" />
          <el-table-column prop="unit" label="单位" width="80" align="center" />
          <el-table-column prop="price" label="单价(元)" width="120" align="right">
            <template #default="{ row }">
              {{ row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="amount" label="小计(元)" width="130" align="right">
            <template #default="{ row }">
              {{ row.amount.toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span class="total-label">合计：</span>
          <span class="total-amount">¥{{ (orderInfo.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </el-card>

      <!-- 操作按钮：按订单状态显示不同按钮，待审核才能审，审核通过才能生成配送任务 -->
      <div class="action-bar">
        <el-button @click="goBack">返回</el-button>
        <template v-if="orderInfo.status === 'pending'">
          <el-button type="success" @click="handleAudit('approved')">审核通过</el-button>
          <el-button type="danger" @click="handleAudit('rejected')">驳回</el-button>
          <el-button type="info" @click="handleCancel">取消订单</el-button>
        </template>
        <template v-if="orderInfo.status === 'approved'">
          <el-button type="primary" @click="handleCreateDelivery">生成配送任务</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
// 订单详情页：从订单列表点进来，展示订单基本信息、收货信息和药品明细快照
// 待审核的订单可以在这里审核通过/驳回/取消，审核通过的订单可以生成配送任务
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, auditOrder, cancelOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(false) // 控制页面加载中的 loading 遮罩
const orderInfo = ref({}) // 订单详情数据，包括订单头和明细 items

// 收货信息：根据下单机构名从表里查对应的联系人信息
// 这里用 computed 而不用在加载数据后手动赋值，是因为订单数据加载完成后它会自动算出来，不用管时机问题
// （说明：收货人信息后端没单独建表，前端先按机构名写死一份模拟数据，答辩时如实说明即可）
const receiverInfo = computed(() => {
  const addressMap = {
    '第一乡镇卫生院': { name: '李院长', phone: '0571-88881111', address: '第一乡镇政府路5号' },
    '第二乡镇卫生院': { name: '周院长', phone: '0571-88882222', address: '第二乡镇健康路10号' },
    '第三乡镇卫生院': { name: '陈院长', phone: '0571-88883333', address: '第三乡镇文化路8号' },
    '东村村卫生室': { name: '王村医', phone: '13800001111', address: '第一乡镇东村' },
    '西村村卫生室': { name: '赵村医', phone: '13800001112', address: '第一乡镇西村' }
  }
  return addressMap[orderInfo.value.institutionName] || { name: '-', phone: '-', address: '-' }
})

// 订单状态映射成 el-tag 的颜色类型，不同状态显示不同颜色，一眼就能区分
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

// 加载订单详情：id 从路由参数里拿（路径是 /order/detail/:id）
const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getOrderDetail(id)
    if (res.code === 200) {
      orderInfo.value = res.data
    }
  } finally {
    // 不管成功失败都要关掉 loading，不然页面会一直转圈
    loading.value = false
  }
}

const goBack = () => {
  router.push('/order')
}

// 审核订单（通过或驳回）：先弹确认框，确认后调接口，成功后重新加载详情刷新状态
const handleAudit = (status) => {
  const title = status === 'approved' ? '审核通过' : '驳回'
  ElMessageBox.confirm(`确定要${title}该订单吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: status === 'approved' ? 'success' : 'warning'
  }).then(async () => {
    const res = await auditOrder(orderInfo.value.id, { status })
    if (res.code === 200) {
      ElMessage.success(title + '成功')
      loadDetail()
    }
  }).catch(() => {}) // 用户点了取消，什么都不用做，空 catch 防止控制台报未处理的 promise 错误
}

// 取消订单：和审核类似，确认后调取消接口再刷新
const handleCancel = () => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await cancelOrder(orderInfo.value.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadDetail()
    }
  }).catch(() => {})
}

// 生成配送任务：目前后端接口还没做，先给个提示占位
const handleCreateDelivery = () => {
  ElMessage.success('配送任务生成功能开发中')
}

// 页面挂载后立刻加载订单数据
onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.detail-header {
  margin-bottom: 16px;
  background-color: #fff;
  border-radius: 4px;
  padding: 12px 20px;
}

.detail-content {
  .info-card {
    margin-bottom: 16px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .item-count {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.total-row {
  margin-top: 16px;
  text-align: right;
  padding-right: 20px;

  .total-label {
    font-size: 14px;
    color: #606266;
  }

  .total-amount {
    font-size: 24px;
    font-weight: bold;
    color: #f56c6c;
    margin-left: 10px;
  }
}

.action-bar {
  margin-top: 20px;
  text-align: center;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}
</style>
