<template>
  <!-- 首页仪表盘容器 - 整个首页的大盒子 -->
  <div class="dashboard-container">
    <!-- 统计卡片区域 - 上面一排四个彩色卡片，展示关键数据 -->
    <el-row :gutter="20" class="stat-cards">
      <!-- 循环渲染每个统计卡片，有药品总数、库存总量、今日订单、待配送任务 -->
      <el-col :xs="12" :sm="12" :md="6" v-for="(card, index) in statCards" :key="index">
        <!-- 单个统计卡片，背景色由卡片数据决定 -->
        <div class="stat-card" :style="{ background: card.color }">
          <div class="stat-title">{{ card.title }}</div>
          <!-- 数字值，大大的那种 -->
          <div class="stat-value">{{ card.value }}</div>
          <!-- 右侧的图标，半透明的装饰用 -->
          <el-icon class="stat-icon"><component :is="card.icon" /></el-icon>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 - 中间的两个图表，左边订单趋势折线图，右边分类占比饼图 -->
    <!-- TODO: 后面可以加个图表切换动画，看起来更酷炫一点 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="14">
        <!-- 订单趋势图表卡片 -->
        <div class="chart-card">
          <!-- 图表头部，标题加切换按钮 -->
          <div class="chart-header">
            <span class="chart-title">订单趋势</span>
            <!-- 切换订单数/订单金额的单选按钮组 -->
            <el-radio-group v-model="trendType" size="small" @change="updateOrderTrend">
              <el-radio-button value="count">订单数</el-radio-button>
              <el-radio-button value="amount">订单金额</el-radio-button>
            </el-radio-group>
          </div>
          <!-- echarts 图表容器，用 ref 获取DOM -->
          <div ref="orderTrendChartRef" class="chart-content"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="10">
        <!-- 药品分类占比图表卡片 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">药品分类占比</span>
          </div>
          <!-- 饼图容器 -->
          <div ref="categoryChartRef" class="chart-content"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 下方区域 - 下面两个表格，库存预警和近期订单 -->
    <el-row :gutter="20" class="bottom-row">
      <el-col :xs="24" :sm="24" :md="12">
        <!-- 库存预警表格卡片 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">库存预警</span>
            <!-- 跳转到库存页面的按钮 -->
            <el-button type="primary" link size="small" @click="$router.push('/inventory')">查看更多</el-button>
          </div>
          <!-- 库存预警表格，显示库存不足的药品 -->
          <el-table :data="warningList" style="width: 100%" size="small">
            <el-table-column prop="drugName" label="药品名称" />
            <!-- 当前库存列，红色加粗显示，提醒用户 -->
            <el-table-column prop="stock" label="当前库存" align="center" width="100">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: bold">{{ row.stock }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="minStock" label="最低库存" align="center" width="100" />
            <el-table-column prop="unit" label="单位" align="center" width="80" />
            <!-- 状态列，显示红色的库存不足标签 -->
            <el-table-column label="状态" align="center" width="100">
              <template #default>
                <el-tag type="danger" size="small">库存不足</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <!-- 近期订单表格卡片 -->
        <div class="chart-card">
          <div class="chart-header">
            <span class="chart-title">近期订单</span>
            <!-- 跳转到订单页面 -->
            <el-button type="primary" link size="small" @click="$router.push('/order')">查看更多</el-button>
          </div>
          <!-- 近期订单表格 -->
          <el-table :data="recentOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="institutionName" label="医疗机构" show-overflow-tooltip />
            <!-- 金额列，右对齐，带人民币符号 -->
            <el-table-column prop="totalAmount" label="金额" align="right" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount.toFixed(2) }}
              </template>
            </el-table-column>
            <!-- 状态列，不同状态不同颜色标签 -->
            <el-table-column label="状态" align="center" width="90">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ row.statusName }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 引入vue的一些API
import { ref, onMounted, onUnmounted, onActivated, nextTick } from 'vue'
// 引入echarts图表库，做数据可视化用
import * as echarts from 'echarts'
// 引入首页相关的接口函数
import { getDashboardStats, getOrderTrend, getDrugCategoryRatio, getStockWarningList, getRecentOrders } from '@/api/dashboard'

// 订单趋势图表的DOM引用（ref，用来初始化echarts）
const orderTrendChartRef = ref(null)
// 药品分类饼图的DOM引用
const categoryChartRef = ref(null)
// 订单趋势图表实例
let orderTrendChart = null
// 分类饼图实例
let categoryChart = null

// 统计卡片数据数组 - 四个卡片，分别是药品总数、库存总量、今日订单、待配送任务
// TODO: 后面可以加个数字滚动动画，数字从0滚到目标值，更有科技感
const statCards = ref([
  { title: '药品总数', value: 0, icon: 'MedicineBox', color: '#417ff9' },
  { title: '库存总量', value: 0, icon: 'Goods', color: '#417ff9' },
  { title: '今日订单', value: 0, icon: 'Document', color: '#417ff9' },
  { title: '待配送任务', value: 0, icon: 'Van', color: '#417ff9' }
])

// 趋势类型，count是按订单数，amount是按订单金额
const trendType = ref('count')
// 库存预警列表数据
const warningList = ref([])
// 近期订单列表数据
const recentOrders = ref([])

// 加载统计数据 - 就是上面那四个卡片的数字
const loadStats = async () => {
  const res = await getDashboardStats()
  if (res.code === 200) {
    statCards.value[0].value = res.data.totalDrugs
    statCards.value[1].value = res.data.totalInventory
    statCards.value[2].value = res.data.todayOrders
    statCards.value[3].value = res.data.pendingTasks
  }
  // console.log('统计数据加载完成', res)  // 调试用，先注释掉
}

// 初始化订单趋势折线图
const initOrderTrendChart = async () => {
  // DOM还没渲染好就直接返回，防止报错
  if (!orderTrendChartRef.value) return
  
  // 用echarts初始化图表
  orderTrendChart = echarts.init(orderTrendChartRef.value)
  // 调用更新函数加载数据
  await updateOrderTrend()
}

// 更新订单趋势图表数据 - 切换订单数/金额的时候也会调用这个函数
const updateOrderTrend = async () => {
  if (!orderTrendChart) return
  
  const res = await getOrderTrend()
  // console.log('订单趋势数据', res.data)  // 调试用，注释掉
  if (res.code === 200) {
    // echarts的配置项，我参考官网例子写的，还挺好用
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: res.data.dates  // x轴是日期
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: trendType.value === 'count' ? '订单数' : '订单金额',
          type: 'line',
          smooth: true,  // 平滑曲线
          // 渐变填充区域，看起来更好看
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          },
          lineStyle: {
            color: '#409eff',
            width: 2
          },
          itemStyle: {
            color: '#409eff'
          },
          // 根据选择的类型显示不同数据
          data: trendType.value === 'count' ? res.data.orderCounts : res.data.orderAmounts
        }
      ]
    }
    orderTrendChart.setOption(option)
  }
}

// 初始化药品分类占比饼图（环形图）
const initCategoryChart = async () => {
  if (!categoryChartRef.value) return
  
  categoryChart = echarts.init(categoryChartRef.value)
  
  const res = await getDrugCategoryRatio()
  if (res.code === 200) {
    // 环形饼图的配置
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      // 图例放在右边，竖着排列
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        itemWidth: 10,
        itemHeight: 10,
        textStyle: {
          fontSize: 12
        }
      },
      series: [
        {
          name: '药品分类',
          type: 'pie',
          radius: ['40%', '70%'],  // 内外半径，做成环形
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 4,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false  // 默认不显示标签，鼠标悬停才显示
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          data: res.data
        }
      ]
    }
    categoryChart.setOption(option)
  }
}

// 加载库存预警列表数据
const loadWarningList = async () => {
  const res = await getStockWarningList()
  if (res.code === 200) {
    warningList.value = res.data
  }
}

// 加载近期订单数据
const loadRecentOrders = async () => {
  const res = await getRecentOrders()
  if (res.code === 200) {
    recentOrders.value = res.data
  }
}

// 根据订单状态返回对应的tag类型（颜色）
// 这个函数好多页面都用到了，后面可以抽成公共工具函数
const getStatusType = (status) => {
  const map = {
    pending: 'warning',    // 待审核-黄色
    approved: 'primary',   // 已审核-蓝色
    delivering: 'success', // 配送中-绿色
    completed: 'info',     // 已完成-灰色
    rejected: 'danger',    // 已驳回-红色
    cancelled: 'info'      // 已取消-灰色
  }
  return map[status] || 'info'
}

// 窗口大小变化时，重新调整图表大小
// 防止窗口缩放后图表变形
const handleResize = () => {
  orderTrendChart?.resize()
  categoryChart?.resize()
}

// 页面挂载完成后执行（首次进入 dashboard 时触发）
onMounted(async () => {
  // 先加载各种数据
  loadStats()
  loadWarningList()
  loadRecentOrders()
  
  // 等DOM更新完再初始化图表，不然可能获取不到宽高
  await nextTick()
  initOrderTrendChart()
  initCategoryChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// keep-alive 被激活时触发（从其他页面切回 dashboard 时）
// 只刷数据、不重新 init 图表（图表已经在 onMounted 里初始化过了）
// 这样库存管理里入库/出库/调拨之后点首页，卡片数字会自动刷新
onActivated(async () => {
  loadStats()
  loadWarningList()
  loadRecentOrders()
  if (orderTrendChart) {
    await updateOrderTrend()
  }
  if (categoryChart) {
    const res = await getDrugCategoryRatio()
    if (res.code === 200) {
      categoryChart.setOption({ series: [{ data: res.data }] })
    }
  }
})

// 页面卸载时清理，防止内存泄漏
onUnmounted(() => {
  orderTrendChart?.dispose()
  categoryChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 仪表盘容器 - 最外层的盒子 */
.dashboard-container {
  padding: 0;
}

/* 统计卡片行 - 上面一排彩色卡片 */
.stat-cards {
  margin-bottom: 20px;
}

/* 单个统计卡片样式 */
.stat-card {
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  position: relative;
  overflow: hidden;
  min-height: 110px;
}

/* 统计卡片标题 */
.stat-title {
  font-size: 14px;
  margin-bottom: 12px;
  opacity: 0.9;
}

/* 统计卡片数值 - 大大的数字 */
.stat-value {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
}

/* 统计卡片右侧的图标装饰 */
.stat-icon {
  font-size: 64px;
  opacity: 0.2;
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
}

/* 图表行和底部行的间距 */
.chart-row,
.bottom-row {
  margin-bottom: 20px;
}

/* 图表卡片 - 白色背景带圆角的卡片容器 */
.chart-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
}

/* 图表头部 - 标题和操作按钮那一行 */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* 图表标题文字 */
.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 图表内容区域 - echarts渲染的地方 */
.chart-content {
  width: 100%;
  height: 300px;
}
</style>
