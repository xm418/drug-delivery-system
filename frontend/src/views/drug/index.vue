<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" @submit.prevent>
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入名称/编码" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="药品分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable style="width: 160px">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增药品</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="drugCode" label="药品编码" width="120" />
      <el-table-column prop="drugName" label="药品名称" min-width="160" />
      <el-table-column prop="categoryName" label="分类" width="130" />
      <el-table-column prop="specification" label="规格" width="130" />
      <el-table-column prop="unit" label="单位" width="70" align="center" />
      <el-table-column prop="manufacturer" label="生产厂家" min-width="180" show-overflow-tooltip />
      <el-table-column prop="price" label="单价(元)" width="100" align="right">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="warningThreshold" label="预警阈值" width="90" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status == 1" type="success" size="small">上架</el-tag>
          <el-tag v-else type="danger" size="small">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="药品编码" prop="drugCode">
          <el-input v-model="form.drugCode" placeholder="请输入药品编码" />
        </el-form-item>
        <el-form-item label="药品名称" prop="drugName">
          <el-input v-model="form.drugName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位，如：盒、瓶、片" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="批准文号" prop="approvalNumber">
          <el-input v-model="form.approvalNumber" placeholder="请输入批准文号" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="warningThreshold">
          <el-input-number v-model="form.warningThreshold" :min="0" />
          <span style="color: #999; font-size: 12px; margin-left: 10px;">库存低于此数量时预警</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述（选填）" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDrugPage, addDrug, updateDrug, deleteDrug, getDrugById } from '@/api/drug'
import { getAllCategoryList } from '@/api/category'

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 分类下拉选项
const categoryList = ref([])

// 搜索条件
const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: null
})

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref([])

// 表单数据
const form = reactive({
  id: null,
  drugCode: '',
  drugName: '',
  categoryId: null,
  specification: '',
  unit: '',
  price: 0,
  manufacturer: '',
  approvalNumber: '',
  warningThreshold: 10,
  status: 1,
  description: ''
})

// 表单校验规则
const rules = {
  drugCode: [{ required: true, message: '请输入药品编码', trigger: 'blur' }],
  drugName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择药品分类', trigger: 'change' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

// 弹窗标题
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑药品' : '新增药品'
})

// 加载分类列表
const loadCategoryList = async () => {
  try {
    const res = await getAllCategoryList()
    if (res.code === 200) {
      categoryList.value = res.data
    }
  } catch (e) {
    console.log('加载分类失败', e)
  }
}

// 加载药品列表数据
const loadData = async () => {
  loading.value = true
  try {
    // 组装查询参数
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || null,
      categoryId: searchForm.categoryId || null,
      status: searchForm.status
    }
    const res = await getDrugPage(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (e) {
    console.error('加载药品列表失败', e)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.status = null
  pageNum.value = 1
  loadData()
}

// 每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  loadData()
}

// 页码改变
const handlePageChange = (val) => {
  pageNum.value = val
  loadData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  // 重置表单
  form.id = null
  form.drugCode = ''
  form.drugName = ''
  form.categoryId = null
  form.specification = ''
  form.unit = ''
  form.price = 0
  form.manufacturer = ''
  form.approvalNumber = ''
  form.warningThreshold = 10
  form.status = 1
  form.description = ''
  dialogVisible.value = true
  // 下次打开清空校验提示
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  // 先查一下详情，保证数据最新
  getDrugById(row.id).then(res => {
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    }
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该药品吗？删除后可在回收站恢复（不过目前还没做回收站功能...）',
    '提示',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '再想想',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDrug(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 删除后如果当前页没数据了，回到上一页
        if (tableData.value.length === 1 && pageNum.value > 1) {
          pageNum.value--
        }
        loadData()
      }
    } catch (e) {
      console.error('删除失败', e)
    }
  }).catch(() => {
    // 用户取消了，不用做什么
  })
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 先校验表单
    await formRef.value.validate()
  } catch (e) {
    // 校验不通过
    return
  }

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateDrug(form)
    } else {
      res = await addDrug(form)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadCategoryList()
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>
