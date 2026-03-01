<template>
  <div class="customer-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="客户名称">
          <el-input v-model="queryForm.name" placeholder="搜姓名/修车厂" clearable />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="queryForm.phone" placeholder="搜电话" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="handleAdd">新增客户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="客户名称" width="180" />
        <el-table-column prop="phone" label="联系电话" width="150" />

        <el-table-column prop="type" label="客户类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'shop' ? 'warning' : ''">
              {{ scope.row.type === 'shop' ? '汽修厂' : '普通散客' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="注册时间" />

        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination v-model:current-page="queryForm.page" v-model:page-size="queryForm.size" :total="total"
          :page-sizes="[10, 20]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData"
          @current-change="loadData" />
      </div>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户或汽修厂名称" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="普通散客 (现结)" value="individual" />
            <el-option label="合作汽修厂 (可挂账)" value="shop" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCustomerList, addCustomer, updateCustomer, deleteCustomer } from '@/api/customer'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryForm = reactive({ page: 1, size: 10, name: '', phone: '' })
const form = reactive({ id: null, name: '', phone: '', type: 'individual' })

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 加载数据
const loadData = () => {
  loading.value = true
  getCustomerList(queryForm).then(res => {
    tableData.value = res.data.list
    total.value = res.data.total
    loading.value = false
  }).catch(() => loading.value = false)
}

const resetQuery = () => {
  queryForm.name = ''
  queryForm.phone = ''
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增客户'
  Object.assign(form, { id: null, name: '', phone: '', type: 'individual' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const api = form.id ? updateCustomer : addCustomer
      api(form).then(() => {
        ElMessage.success(form.id ? '修改成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      })
    }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该客户吗？', '警告', { type: 'warning' })
    .then(() => {
      deleteCustomer(id).then(() => {
        ElMessage.success('删除成功')
        loadData()
      })
    })
}

onMounted(() => { loadData() })
</script>

<style scoped>
.customer-container {
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