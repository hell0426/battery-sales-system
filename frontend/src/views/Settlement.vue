<template>
  <div class="settlement-container">
    <el-row :gutter="20">
      <!-- 左侧：订单列表 -->
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
            <el-table-column label="客户/实收金额">
              <template #default="scope">
                <div style="font-weight: bold">
                  {{ getCustomerName(scope.row.customerId) }}
                </div>
                <div style="color: #f56c6c; font-size: 12px">
                  实收: ¥{{ scope.row.totalAmount }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="日期" width="100">
              <template #default="scope">
                {{ scope.row.createTime ? scope.row.createTime.substring(5, 10) : "" }}
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

      <!-- 右侧：订单详情 -->
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

          <!-- 🔴 重点修改：订单概况展示 -->
          <div class="order-info-box">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="客户名称">{{
                getCustomerName(currentOrder.customerId)
              }}</el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="currentOrder.status === 'paid' ? 'success' : 'danger'">
                  {{ currentOrder.status === "paid" ? "已结清" : "挂账未付" }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="下单时间" :span="2">
                {{
                  currentOrder.createTime ? currentOrder.createTime.replace("T", " ") : ""
                }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 商品明细表格 -->
          <el-table
            :data="detailList"
            border
            stripe
            style="margin-top: 20px"
            size="small"
          >
            <el-table-column prop="productName" label="电瓶型号" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="price" label="成交单价">
              <template #default="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column label="小计">
              <template #default="scope"
                >¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template
              >
            </el-table-column>
          </el-table>

          <!-- 🔴 重点修改：金额计算明细区 -->
          <div class="money-summary">
            <div class="summary-item">
              <span>商品总价：</span>
              <span
                >¥
                {{
                  (currentOrder.totalAmount + currentOrder.discountAmount).toFixed(2)
                }}</span
              >
            </div>
            <div class="summary-item discount">
              <span>优惠减免：</span>
              <span>- ¥ {{ currentOrder.discountAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-item final">
              <span>实收合计：</span>
              <span class="final-price">¥ {{ currentOrder.totalAmount.toFixed(2) }}</span>
            </div>
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
import { getItemsByOrderId } from "@/api/orderItem";
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const customerMap = ref({});
const detailList = ref([]);
const currentOrder = ref({});

const queryForm = reactive({ page: 1, size: 10, status: "debt" });

const loadCustomers = async () => {
  const res = await getCustomerList({ page: 1, size: 1000 });
  res.data.list.forEach((c) => {
    customerMap.value[c.id] = c.name;
  });
};
const getCustomerName = (id) => customerMap.value[id] || "未知客户";

const loadData = () => {
  loading.value = true;
  getOrderList(queryForm).then((res) => {
    tableData.value = res.data.list;
    total.value = res.data.total;
    loading.value = false;
    currentOrder.value = {};
    detailList.value = [];
  });
};

const handleRowClick = (row) => {
  currentOrder.value = row;
  // 确保折扣金额有默认值
  if (!currentOrder.value.discountAmount) currentOrder.value.discountAmount = 0;

  getItemsByOrderId(row.id).then((res) => {
    detailList.value = res.data.items;
  });
};

const handleSettle = (id) => {
  ElMessageBox.confirm("确认已收到该笔款项？", "结账确认").then(() => {
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
.list-card,
.detail-card {
  height: calc(100vh - 120px);
  overflow-y: auto;
}

/* 🔴 金额汇总区域样式 */
.money-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fb;
  border-radius: 8px;
  text-align: right;
}
.summary-item {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}
.discount {
  color: #f56c6c;
}
.final {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
  font-weight: bold;
}
.final-price {
  color: #f56c6c;
  font-size: 24px;
}
.pagination-area {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}
</style>
