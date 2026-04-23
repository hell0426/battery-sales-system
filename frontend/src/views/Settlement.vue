<template>
  <div class="settlement-container">
    <el-row :gutter="20">
      <!-- 左侧：订单列表 (占 10 份宽度) -->
      <el-col :span="10">
        <el-card shadow="never" class="list-card">
          <template #header>
            <div class="card-header">
              <span>📋 待对账列表</span>
              <el-select
                v-model="queryForm.status"
                size="small"
                style="width: 100px"
                @change="loadData"
              >
                <el-option label="未结清" value="debt" />
                <el-option label="已结清" value="paid" />
              </el-select>
            </div>
          </template>

          <el-table
            :data="tableData"
            v-loading="loading"
            highlight-current-row
            @row-click="handleRowClick"
            style="cursor: pointer"
          >
            <el-table-column label="客户/金额">
              <template #default="scope">
                <div style="font-weight: bold">
                  {{ getCustomerName(scope.row.customerId) }}
                </div>
                <div style="color: #f56c6c; font-size: 12px">
                  ¥{{ scope.row.totalAmount }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="日期" width="100">
              <template #default="scope">
                {{ scope.row.createTime.substring(5, 10) }}
                <!-- 只显示月-日 -->
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-area">
            <el-pagination
              layout="prev, pager, next"
              :total="total"
              small
              @current-change="loadData"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：订单详情 (占 14 份宽度) -->
      <el-col :span="14">
        <el-card v-if="currentOrder.id" shadow="never" class="detail-card">
          <template #header>
            <div class="detail-header">
              <span>📄 订单详情 (单号: {{ currentOrder.id }})</span>
              <el-button
                v-if="currentOrder.status === 'debt'"
                type="success"
                icon="Check"
                @click="handleSettle(currentOrder.id)"
                >确认收款结账</el-button
              >
            </div>
          </template>

          <div class="order-info">
            <p>
              <strong>客户名称：</strong>{{ getCustomerName(currentOrder.customerId) }}
            </p>
            <p><strong>下单时间：</strong>{{ currentOrder.createTime }}</p>
            <p>
              <strong>当前状态：</strong>
              <el-tag :type="currentOrder.status === 'paid' ? 'success' : 'danger'">
                {{ currentOrder.status === "paid" ? "已结清" : "挂账未付" }}
              </el-tag>
            </p>
          </div>

          <el-table :data="detailList" border stripe style="margin-top: 20px">
            <el-table-column prop="productName" label="电瓶型号" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="price" label="成交价">
              <template #default="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
          </el-table>

          <div class="total-bar">
            应收合计：<span>¥{{ currentOrder.totalAmount }}</span>
          </div>
        </el-card>

        <!-- 未选择订单时的提示 -->
        <el-empty v-else description="请点击左侧订单查看明细" :image-size="100" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getOrderList, settleOrder } from "@/api/orders";
import { getCustomerList } from "@/api/customer";
import { getItemsByOrderId } from "@/api/orderItem"; // 🔴 记得在 api 文件夹建这个
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const customerMap = ref({});
const detailList = ref([]); // 详情数据
const currentOrder = ref({}); // 当前选中的订单对象

const queryForm = reactive({ page: 1, size: 10, status: "debt" });

// 1. 加载客户映射
const loadCustomers = async () => {
  const res = await getCustomerList({ page: 1, size: 1000 });
  res.data.list.forEach((c) => {
    customerMap.value[c.id] = c.name;
  });
};
const getCustomerName = (id) => customerMap.value[id] || "未知客户";

// 2. 加载左侧列表
const loadData = () => {
  loading.value = true;
  getOrderList(queryForm).then((res) => {
    tableData.value = res.data.list;
    total.value = res.data.total;
    loading.value = false;
    // 自动清除右侧显示
    currentOrder.value = {};
    detailList.value = [];
  });
};

// 🔴 3. 点击左侧行，加载右侧明细
const handleRowClick = (row) => {
  currentOrder.value = row;
  getItemsByOrderId(row.id).then((res) => {
    detailList.value = res.data.items;
  });
};

// 4. 结账
const handleSettle = (id) => {
  ElMessageBox.confirm("确认收款？", "提示").then(() => {
    settleOrder(id).then(() => {
      ElMessage.success("结账成功");
      loadData();
    });
  });
};

onMounted(async () => {
  await loadCustomers();
  loadData();
});
</script>

<style scoped>
.settlement-container {
  padding: 0;
}
.card-header,
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.list-card {
  height: 80vh;
  overflow-y: auto;
}
.detail-card {
  height: 80vh;
}
.order-info p {
  margin: 10px 0;
  font-size: 14px;
}
.total-bar {
  margin-top: 30px;
  text-align: right;
  font-size: 18px;
  font-weight: bold;
}
.total-bar span {
  color: #f56c6c;
  font-size: 24px;
}
.pagination-area {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}
</style>
