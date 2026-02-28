<template>
  <div class="product-container">

    <!-- 1. 顶部搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="品牌">
          <el-input v-model="queryForm.brand" placeholder="请输入品牌" clearable />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="queryForm.model" placeholder="请输入型号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="handleAdd">新增电瓶</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 数据表格区域 -->
    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="model" label="型号" width="150" />

        <el-table-column prop="costPrice" label="进价(元)" width="120">
          <template #default="scope">
            <span style="color: #909399">¥{{ scope.row.costPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="sellingPrice" label="售价(元)" width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.sellingPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.stock < 10 ? 'danger' : 'success'">
              {{ scope.row.stock }} 个
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="录入时间" width="180" />

        <el-table-column label="操作" min-width="150">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination v-model:current-page="queryForm.page" v-model:page-size="queryForm.size"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 3. 新增/编辑 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="例如：风帆" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="例如：6-QW-45" />
        </el-form-item>
        <el-form-item label="进价" prop="costPrice">
          <el-input-number v-model="form.costPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="售价" prop="sellingPrice">
          <el-input-number v-model="form.sellingPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProductList, addProduct, updateProduct, deleteProduct } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 基础数据 ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增电瓶')
const formRef = ref(null) // 表单引用

// 查询条件
const queryForm = reactive({
  page: 1,
  size: 10,
  brand: '',
  model: ''
})

// 表单数据
const form = reactive({
  id: null,
  brand: '',
  model: '',
  costPrice: 0,
  sellingPrice: 0,
  stock: 0
})

// 表单验证规则
const rules = {
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  costPrice: [{ required: true, message: '请输入进价', trigger: 'blur' }],
  sellingPrice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

// --- 核心逻辑 ---

// 1. 加载列表
const loadData = () => {
  loading.value = true
  getProductList(queryForm).then(res => {
    tableData.value = res.data.list
    total.value = res.data.total
    loading.value = false
  }).catch(() => loading.value = false)
}

// 2. 重置查询
const resetQuery = () => {
  queryForm.brand = ''
  queryForm.model = ''
  loadData()
}

// 3. 点击新增
const handleAdd = () => {
  dialogTitle.value = '新增电瓶'
  // 清空表单
  Object.assign(form, { id: null, brand: '', model: '', costPrice: 0, sellingPrice: 0, stock: 0 })
  dialogVisible.value = true
}

// 4. 点击编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑电瓶'
  // 回显数据 (复制一份 row 给 form，避免直接修改表格)
  Object.assign(form, row)
  dialogVisible.value = true
}

// 5. 提交表单 (新增或修改)
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        // 有ID，说明是修改
        updateProduct(form).then(res => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          loadData() // 刷新列表
        })
      } else {
        // 没ID，说明是新增
        addProduct(form).then(res => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          loadData() // 刷新列表
        })
      }
    }
  })
}

// 6. 点击删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个电瓶吗？删除后无法恢复！', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteProduct(id).then(res => {
      ElMessage.success('删除成功')
      loadData()
    })
  })
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.product-container {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-area {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>