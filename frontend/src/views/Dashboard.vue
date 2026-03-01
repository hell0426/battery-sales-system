<template>
  <div class="dashboard-container">

    <!-- 1. 数据卡片区 -->
    <el-row :gutter="20" class="card-row">
      <el-col :span="8">
        <el-card shadow="hover" class="data-card sales-card">
          <div class="card-header">
            <span>💰 总销售额 (已入账)</span>
          </div>
          <div class="card-num">¥ {{ statData.totalSales }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card debt-card">
          <div class="card-header">
            <span>📝 待收欠款 (挂账)</span>
          </div>
          <div class="card-num">¥ {{ statData.totalDebt }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="data-card stock-card">
          <div class="card-header">
            <span>🚨 库存预警商品</span>
          </div>
          <div class="card-num">{{ statData.lowStockCount }} <span style="font-size:14px">种</span></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 图表区 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>📊 电瓶品牌库存占比</span></template>
          <!-- ECharts 容器 -->
          <div id="pieChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>📈 快捷功能入口</span></template>
          <div class="quick-entry">
            <el-button type="primary" size="large" icon="ShoppingCart" @click="$router.push('/sales')">去开单</el-button>
            <el-button type="success" size="large" icon="Box" @click="$router.push('/product')">查库存</el-button>
            <el-button type="warning" size="large" icon="Notebook" @click="$router.push('/settlement')">查欠账</el-button>
          </div>
          <div style="margin-top: 20px; color: #999">
            <p>系统运行状态：正常</p>
            <p>当前时间：{{ new Date().toLocaleString() }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getStatData } from '@/api/stat'

const statData = reactive({
  totalSales: 0,
  totalDebt: 0,
  lowStockCount: 0
})

// 初始化饼图
const initPieChart = (dataList) => {
  const chartDom = document.getElementById('pieChart')
  const myChart = echarts.init(chartDom)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '品牌库存',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: dataList // 后端传来的数据
      }
    ]
  }
  myChart.setOption(option)
}

// 加载数据
onMounted(() => {
  getStatData().then(res => {
    const data = res.data.info
    statData.totalSales = data.totalSales
    statData.totalDebt = data.totalDebt
    statData.lowStockCount = data.lowStockCount

    // 渲染图表
    initPieChart(data.brandPieList)
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.card-header {
  font-size: 14px;
  color: #666;
}

.card-num {
  font-size: 28px;
  font-weight: bold;
  margin-top: 10px;
}

.sales-card .card-num {
  color: #67C23A;
}

.debt-card .card-num {
  color: #F56C6C;
}

.stock-card .card-num {
  color: #E6A23C;
}

.quick-entry {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}
</style>