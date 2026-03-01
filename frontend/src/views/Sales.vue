<template>
  <div class="sales-container">
    <el-row :gutter="20" style="height: 100%">

      <!-- 左侧：商品选择区 -->
      <el-col :span="14">
        <el-card class="product-area">
          <template #header>
            <div class="card-header">
              <span>商品列表 (点击加入购物车)</span>
              <el-input v-model="searchText" placeholder="搜品牌/型号..." style="width: 200px" @input="loadProducts" />
            </div>
          </template>

          <!-- 商品网格 -->
          <div class="product-grid">
            <div v-for="p in productList" :key="p.id" class="product-item" @click="addToCart(p)">
              <div class="p-brand">{{ p.brand }}</div>
              <div class="p-model">{{ p.model }}</div>
              <div class="p-price">¥{{ p.sellingPrice }}</div>
              <div class="p-stock" :class="{ 'no-stock': p.stock <= 0 }">库存: {{ p.stock }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：购物车 & 结算区 -->
      <el-col :span="10">
        <el-card class="cart-area">
          <template #header>
            <span>销售清单</span>
          </template>

          <!-- 购物车列表 -->
          <el-table :data="cartList" height="400" stripe>
            <el-table-column prop="brand" label="商品" width="120">
              <template #default="scope">{{ scope.row.brand }} {{ scope.row.model }}</template>
            </el-table-column>
            <el-table-column label="数量" width="140">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.maxStock" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="小计">
              <template #default="scope">
                <span style="color: red; font-weight: bold">¥{{ scope.row.quantity * scope.row.sellingPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column width="60">
              <template #default="scope">
                <el-button type="danger" icon="Delete" circle size="small" @click="removeFromCart(scope.$index)" />
              </template>
            </el-table-column>
          </el-table>

          <!-- 结算表单 -->
          <div class="checkout-area">
            <div class="total-price">总金额：<span>¥{{ totalAmount }}</span></div>

            <el-form label-width="80px" style="margin-top: 20px">
              <el-form-item label="选择客户">
                <el-select v-model="customerId" placeholder="请选择客户" filterable style="width: 100%">
                  <el-option v-for="c in customerList" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </el-form-item>

              <el-form-item label="支付方式">
                <el-radio-group v-model="payStatus">
                  <el-radio label="paid" border>现金/微信 (已结)</el-radio>
                  <el-radio label="debt" border style="margin-left: 10px; color: red">挂账 (未付)</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-button type="primary" size="large" style="width: 100%" @click="handleSubmit" :loading="submitting">
                确认开单
              </el-button>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProductList } from '@/api/product'
import { getCustomerList } from '@/api/customer'
import { submitOrder } from '@/api/orders'
import { ElMessage } from 'element-plus'

// --- 数据定义 ---
const productList = ref([])
const customerList = ref([])
const cartList = ref([])
const searchText = ref('')
const customerId = ref(null)
const payStatus = ref('paid')
const submitting = ref(false)

// 计算总金额
const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.quantity * item.sellingPrice, 0)
})

// --- 方法 ---

// 1. 加载商品 (这里偷懒一次性取100个，实际应该分页)
const loadProducts = () => {
  getProductList({ page: 1, size: 100, brand: searchText.value }).then(res => {
    productList.value = res.data.list
  })
}

// 2. 加载客户
const loadCustomers = () => {
  getCustomerList({ page: 1, size: 100 }).then(res => {
    customerList.value = res.data.list
  })
}

// 3. 加入购物车
const addToCart = (product) => {
  if (product.stock <= 0) {
    return ElMessage.warning('库存不足！')
  }
  const existItem = cartList.value.find(item => item.id === product.id)
  if (existItem) {
    if (existItem.quantity < product.stock) {
      existItem.quantity++
    } else {
      ElMessage.warning('不能超过最大库存')
    }
  } else {
    cartList.value.push({
      id: product.id,
      brand: product.brand,
      model: product.model,
      sellingPrice: product.sellingPrice,
      quantity: 1,
      maxStock: product.stock
    })
  }
}

// 4. 移除购物车
const removeFromCart = (index) => {
  cartList.value.splice(index, 1)
}

// 5. 提交订单 (核心)
const handleSubmit = () => {
  if (cartList.value.length === 0) return ElMessage.warning('购物车是空的')
  if (!customerId.value) return ElMessage.warning('请选择客户')

  submitting.value = true

  // 组装发给后端的数据
  const submitData = {
    customerId: customerId.value,
    status: payStatus.value,
    items: cartList.value.map(item => ({
      productId: item.id,
      quantity: item.quantity,
      price: item.sellingPrice
    }))
  }

  submitOrder(submitData).then(() => {
    ElMessage.success('开单成功！')
    cartList.value = [] // 清空购物车
    loadProducts()      // 刷新商品库存
    submitting.value = false
  }).catch(() => submitting.value = false)
}

onMounted(() => {
  loadProducts()
  loadCustomers()
})
</script>

<style scoped>
.sales-container {
  height: 85vh;
}

.product-area,
.cart-area {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 商品网格样式 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 15px;
  overflow-y: auto;
  height: 500px;
}

.product-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  background: white;
}

.product-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.p-brand {
  font-size: 14px;
  color: #606266;
}

.p-model {
  font-weight: bold;
  font-size: 16px;
  margin: 5px 0;
}

.p-price {
  color: #f56c6c;
  font-weight: bold;
}

.p-stock {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.no-stock {
  color: red;
}

.checkout-area {
  border-top: 2px dashed #ebeef5;
  padding-top: 20px;
  margin-top: auto;
}

.total-price {
  font-size: 18px;
  text-align: right;
  margin-bottom: 10px;
}

.total-price span {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>