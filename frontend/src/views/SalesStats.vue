<template>
  <div class="sales-stats-container">
    <!-- 1. 多条件搜索栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadData"
          />
        </el-form-item>
        <el-form-item label="品牌">
          <el-select
            v-model="queryForm.brand"
            placeholder="全部品牌"
            clearable
            @change="loadData"
          >
            <el-option
              v-for="item in brandOptions"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="型号">
          <el-input
            v-model="queryForm.model"
            placeholder="输入型号"
            clearable
            @input="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">开始统计</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 数据汇总卡片 (显示在表格上方) -->
    <div class="summary-bar">
      <el-tag size="large"
        >符合条件的总计金额：<span class="money">¥ {{ summaryAmount }}</span></el-tag
      >
      <el-tag type="success" size="large" style="margin-left: 10px"
        >成交总件数：{{ total }} 件</el-tag
      >
    </div>

    <!-- 3. 销售明细表格 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        show-summary
        :summary-method="getSummaries"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="productName" label="电瓶信息" min-width="200" />
        <el-table-column prop="price" label="销售单价" width="120">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column label="小计" width="120">
          <template #default="scope">
            <span style="font-weight: bold; color: #f56c6c"
              >¥{{ scope.row.price * scope.row.quantity }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="成交时间" width="180" />
      </el-table>

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
import { ref, reactive, onMounted } from "vue";
import { getSalesStats } from "@/api/orders";
import { getBrandList } from "@/api/category";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const summaryAmount = ref(0);
const brandOptions = ref([]);

const queryForm = reactive({
  page: 1,
  size: 10,
  brand: "",
  model: "",
  dateRange: [],
});

const loadData = () => {
  loading.value = true;
  getSalesStats(queryForm)
    .then((res) => {
      tableData.value = res.data.list;
      total.value = res.data.total;
      summaryAmount.value = res.data.summaryAmount;
      loading.value = false;
    })
    .catch(() => (loading.value = false));
};

const loadBrands = () => {
  getBrandList().then((res) => (brandOptions.value = res.data.items));
};

const resetQuery = () => {
  Object.assign(queryForm, { page: 1, size: 10, brand: "", model: "", dateRange: [] });
  loadData();
};

onMounted(() => {
  loadData();
  loadBrands();
});
</script>

<style scoped>
.filter-card {
  margin-bottom: 20px;
}
.summary-bar {
  margin-bottom: 15px;
  padding: 0 5px;
}
.money {
  font-weight: bold;
  font-size: 18px;
  color: #f56c6c;
}
.pagination-area {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
