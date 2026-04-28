<template>
  <div class="sales-stats-container">
    <!-- 1. 多条件搜索栏 -->
    <el-card class="filter-card">
      <el-form :model="queryForm" size="default" label-width="80px">
        <!-- 第一行：时间、品牌、型号 -->
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="queryForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="loadData"
              />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="品牌">
              <el-select
                v-model="queryForm.brand"
                placeholder="全部品牌"
                clearable
                style="width: 100%"
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
          </el-col>
          <el-col :span="7">
            <el-form-item label="型号">
              <el-input
                v-model="queryForm.model"
                placeholder="输入型号"
                clearable
                style="width: 100%"
                @input="loadData"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行：客户名称、销售员、统计按钮 -->
        <el-row :gutter="20" style="margin-top: 10px">
          <el-col :span="7">
            <el-form-item label="客户名称">
              <el-select
                v-model="queryForm.customerName"
                placeholder="选择客户"
                clearable
                style="width: 100%"
                @change="loadData"
              >
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7" v-if="queryForm.role === 'admin'">
            <el-form-item label="销售员">
              <!-- 修正：确保老板能选所有人，员工只能看自己 -->
              <el-select
                v-model="queryForm.userName"
                placeholder="全部员工"
                clearable
                style="width: 100%"
                @change="loadData"
              >
                <el-option
                  v-for="u in userOptions"
                  :key="u.id"
                  :label="u.realName"
                  :value="u.realName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="10" style="display: flex; gap: 10px; padding-left: 20px">
            <el-button type="primary" icon="Search" @click="loadData">开始统计</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            <el-button type="warning" icon="Download" @click="handleExport"
              >导出Excel</el-button
            >
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 2. 数据汇总卡片 -->
    <div class="summary-bar">
      <el-tag size="large"
        >符合条件的总计金额：<span class="money"
          >¥ {{ summaryAmount.toFixed(2) }}</span
        ></el-tag
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
        <el-table-column prop="customerName" label="购买客户" width="150" />
        <el-table-column prop="userName" label="销售员" width="120">
          <template #default="scope">
            <el-tag size="small" effect="plain">{{
              scope.row.userName || "系统管理员"
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="电瓶信息" min-width="200" />
        <el-table-column prop="price" label="销售单价" width="120">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column label="小计" width="120">
          <template #default="scope">
            <span style="font-weight: bold; color: #f56c6c"
              >¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="成交时间" width="180">
          <template #default="scope">
            <!-- 修正：去掉 T 字符，显示更规范 -->
            {{ scope.row.createTime ? scope.row.createTime.replace("T", " ") : "" }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getSalesStats, exportSalesExcel } from "@/api/orders";
import { getBrandList } from "@/api/category";
import { getCustomerList } from "@/api/customer";
import { getUserList } from "@/api/user"; // 新增员工接口导入
import { ElMessage } from "element-plus";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const summaryAmount = ref(0);
const brandOptions = ref([]);
const customerOptions = ref([]);
const userOptions = ref([]); // 新增：定义员工列表

const queryForm = reactive({
  page: 1,
  size: 10,
  brand: "",
  model: "",
  customerName: "", // 补全字段
  userName: "", // 补全字段
  dateRange: [],
  role: "",
  userId: null,
});

const getSummaries = (param) => {
  const { columns, data } = param;
  const sums = [];
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = "合计";
      return;
    }
    if (column.label === "数量" || column.label === "小计") {
      const values = data.map((item) => {
        if (column.label === "小计") return Number(item.price * item.quantity);
        return Number(item[column.property]);
      });
      if (!values.every((value) => isNaN(value))) {
        const res = values.reduce((prev, curr) => prev + curr, 0);
        sums[index] = column.label === "小计" ? "¥ " + res.toFixed(2) : res;
      }
    } else {
      sums[index] = "";
    }
  });
  return sums;
};

const handleExport = () => {
  ElMessage.info("正在生成报表，请稍候...");
  exportSalesExcel(queryForm).then((res) => {
    const blob = new Blob([res], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    link.download = `销售统计报表_${new Date().getTime()}.xlsx`;
    link.click();
    window.URL.revokeObjectURL(link.href);
    ElMessage.success("导出成功！");
  });
};

const loadData = () => {
  // 从本地缓存同步身份信息
  queryForm.userId = sessionStorage.getItem("userId");
  queryForm.role = sessionStorage.getItem("userRole");

  loading.value = true;
  getSalesStats(queryForm)
    .then((res) => {
      tableData.value = res.data.list;
      total.value = res.data.total;
      summaryAmount.value = res.data.summaryAmount || 0;
    })
    .finally(() => {
      loading.value = false;
    });
};

const loadBrands = () => {
  getBrandList().then((res) => (brandOptions.value = res.data.items));
};
const loadCustomers = () => {
  getCustomerList({ page: 1, size: 500 }).then(
    (res) => (customerOptions.value = res.data.list),
  );
};
const loadUsers = () => {
  getUserList().then((res) => (userOptions.value = res.data.items));
}; // 新增获取员工

const resetQuery = () => {
  // 重置所有字段
  Object.assign(queryForm, {
    page: 1,
    size: 10,
    brand: "",
    model: "",
    customerName: "",
    userName: "",
    dateRange: [],
  });
  loadData();
};

onMounted(() => {
  loadData();
  loadBrands();
  loadCustomers();
  loadUsers(); // 🔴 页面加载时执行
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
