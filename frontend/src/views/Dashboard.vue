<template>
  <div class="dashboard-container">
    <!-- 1. 顶部统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card
          shadow="hover"
          class="data-card today-card"
          @click="$router.push('/sales')"
        >
          <div class="card-label">☀️ 今日销售额 (去开单)</div>
          <div class="card-value">¥ {{ statData.todaySales }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card
          shadow="hover"
          class="data-card sales-card"
          @click="$router.push('/settlement')"
        >
          <div class="card-label">💰 累计已收金额 (去对账)</div>
          <div class="card-value">¥ {{ statData.totalSales }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card
          shadow="hover"
          class="data-card debt-card"
          @click="$router.push('/settlement')"
        >
          <div class="card-label">📝 待收挂账总额 (去催款)</div>
          <div class="card-value">¥ {{ statData.totalDebt }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card
          shadow="hover"
          class="data-card warn-card"
          @click="$router.push('/product')"
        >
          <div class="card-label">🚨 库存预警型号 (去补货)</div>
          <div class="card-value">{{ statData.lowStockCount }} <small>种</small></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 中间大图表：销售趋势 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span class="header-title">📈 销售额趋势分析</span>
              <el-radio-group v-model="trendType" size="small" @change="renderTrendChart">
                <el-radio-button label="month">最近30天</el-radio-button>
                <el-radio-button label="year">年度月份</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="trendChart" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 底部对比图表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header
            ><span class="header-title">🏆 热门型号销量排行 (TOP 5)</span></template
          >
          <div id="rankingChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="header-title">📊 业务构成分析</span></template>
          <div class="pie-group">
            <div id="customerPie" style="width: 50%; height: 300px"></div>
            <div id="stockPie" style="width: 50%; height: 300px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import * as echarts from "echarts";
import { getStatData } from "@/api/stat";

const statData = reactive({
  todaySales: 0,
  totalSales: 0,
  totalDebt: 0,
  lowStockCount: 0,
});
const trendType = ref("month");
let allData = {}; // 存储后端返回的完整对象
let trendChart = null;

// 渲染趋势图 (支持切换)
const renderTrendChart = () => {
  const data =
    trendType.value === "month" ? allData.dailySalesTrend : allData.monthlySalesTrend;
  if (!trendChart) trendChart = echarts.init(document.getElementById("trendChart"));

  trendChart.setOption(
    {
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: data.map((i) => i.name), boundaryGap: false },
      yAxis: { type: "value" },
      series: [
        {
          name: "销售额",
          type: "line",
          smooth: true,
          data: data.map((i) => i.value),
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "#409eff" },
              { offset: 1, color: "#fff" },
            ]),
          },
          itemStyle: { color: "#409eff" },
        },
      ],
    },
    true,
  );
};

// 渲染其他图表
const initOtherCharts = (data) => {
  // 销量排行
  const rankingChart = echarts.init(document.getElementById("rankingChart"));
  rankingChart.setOption({
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "8%", bottom: "3%", containLabel: true },
    xAxis: { type: "value" },
    yAxis: {
      type: "category",
      data: data.brandSalesRanking.map((i) => i.name).reverse(),
    },
    series: [
      {
        type: "bar",
        data: data.brandSalesRanking.map((i) => i.value).reverse(),
        itemStyle: { color: "#67c23a" },
      },
    ],
  });

  const customerPie = echarts.init(document.getElementById("customerPie"));
  customerPie.setOption({
    title: { text: "客户分布", left: "center", bottom: 10 },
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        center: ["50%", "45%"],
        radius: "65%", // 稍微缩小一点，显得精致
        label: {
          show: true,
          position: "inside",
          formatter: "{b}",
          color: "#fff",
          fontSize: 11,
        },
        data: data.customerTypeDist.map((i) => ({
          name: i.name === "shop" ? "汽修厂" : "散客",
          value: i.value,
        })),
      },
    ],
  });

  // 3. 库存占比环形图 (优化版)
  const stockPie = echarts.init(document.getElementById("stockPie"));
  stockPie.setOption({
    title: {
      text: "库存占比",
      left: "30%", // 圆心左移了，标题也要跟着左移
      top: "center",
      textStyle: { fontSize: 13 },
    },
    tooltip: { trigger: "item", formatter: "{b}: {c}个 ({d}%)" },
    legend: {
      orient: "vertical",
      right: "0", // 靠最右边
      top: "middle",
      itemWidth: 8,
      itemHeight: 8,
      textStyle: { fontSize: 10 },
      // 如果品牌名太长，这里可以限制一下显示长度
      formatter: function (name) {
        return name.length > 8 ? name.slice(0, 8) + "..." : name;
      },
    },
    series: [
      {
        type: "pie",
        center: ["35%", "50%"], // 关键：圆心从 50% 移到 35%，给右边图例留出大量空间
        radius: ["40%", "65%"], // 略微缩小外径，防止溢出
        avoidLabelOverlap: true,
        label: {
          show: true,
          position: "inside",
          formatter: "{d}%",
          fontSize: 9, // 字号调小一点，防止重叠
          color: "#fff",
        },
        // 关键：如果占比太小（比如那个1.46%），就不显示里面的文字，防止重叠
        minShowLabelAngle: 5,
        data: data.brandPieList,
        itemStyle: { borderRadius: 4, borderColor: "#fff", borderWidth: 2 },
      },
    ],
  });
};

onMounted(() => {
  getStatData().then((res) => {
    allData = res.data.info;
    Object.assign(statData, allData);
    renderTrendChart();
    initOtherCharts(allData);
  });
});
</script>

<style scoped>
.dashboard-container {
  padding: 0px;
}
.data-card {
  color: white;
  cursor: pointer;
  border: none;
}
.card-label {
  font-size: 13px;
  opacity: 0.8;
}
.card-value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 8px;
}

.today-card {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
}
.sales-card {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
}
.debt-card {
  background: linear-gradient(135deg, #f56c6c 0%, #fab6b6 100%);
}
.warn-card {
  background: linear-gradient(135deg, #e6a23c 0%, #f3d19e 100%);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.pie-group {
  display: flex;
}
</style>
