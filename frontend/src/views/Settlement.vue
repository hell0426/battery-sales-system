<template>
  <div class="settlement-container">
    <!-- 顶部卡片 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 150px" @change="loadData">
            <el-option label="❌ 未结清 (挂账)" value="debt" />
            <el-option label="✅ 已结清" value="paid" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="订单号" width="80" align="center" />
        
        <el-table-column label="客户名称" width="180">
          <template #default="scope">
            <!-- 自动从客户列表里匹配名字 -->
            {{ getCustomerName(scope.row.customerId) }}
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="总金额" width="150">
          <template #default="scope">
            <span style="font-weight: bold; font-size: 16px;">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'paid'" type="success" effect="dark">已结清</el-tag>
            <el-tag v-else type="danger" effect="dark">挂账未付</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="开单时间" width="180" />

        <el-table-column label="操作">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'debt'"
              type="primary" 
              size="small" 
              icon="Money"
              @click="handleSettle(scope.row.id)"
            >
              一键结账
            </el-button>
            <span v-else style="color: #909399; font-size: 12px;">无需操作</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList, settleOrder } from '@/api/orders'
import { getCustomerList } from '@/api/customer' // 用来翻译ID
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const customerMap = ref({}) // 存客户ID和名字的对应关系

const queryForm = reactive({
  page: 1,
  size: 10,
  status: 'debt' // 默认进来只看欠钱的
})

// 1. 加载所有客户 (为了把ID翻译成名字)
const loadCustomers = async () => {
  const res = await getCustomerList({ page: 1, size: 1000 })
  // 把数组转成对象: {1: "张三", 2: "修理厂"}，方便查找
  res.data.list.forEach(c => {
    customerMap.value[c.id] = c.name
  })
}

// 辅助方法：ID转名字
const getCustomerName = (id) => {
  return customerMap.value[id] || '未知客户(ID:' + id + ')'
}

// 2. 加载订单
const loadData = () => {
  loading.value = true
  getOrderList(queryForm).then(res => {
    tableData.value = res.data.list
    total.value = res.data.total
    loading.value = false
  }).catch(() => loading.value = false)
}

// 3. 结账操作
const handleSettle = (id) => {
  ElMessageBox.confirm('确认该客户已付款？结账后状态将变为[已结清]。', '收款确认', {
    confirmButtonText: '确认已收钱',
    type: 'success'
  }).then(() => {
    settleOrder(id).then(() => {
      ElMessage.success('结账成功！')
      loadData() // 刷新列表
    })
  })
}

// 初始化
onMounted(async () => {
  await loadCustomers() // 先查客户
  loadData()            // 再查订单
})
</script>

<style scoped>
.settlement-container { padding: 0; }
.search-card { margin-bottom: 20px; }
.pagination-area { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>