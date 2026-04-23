<template>
  <div class="sales-container">
    <el-row :gutter="20" style="height: 100%">
      <!-- 左侧：商品选择区 -->
      <el-col :span="14">
        <el-card class="product-area" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="title">📦 商品选购</span>
            </div>
          </template>

          <!-- 品牌分类标签页 -->
          <el-tabs v-model="activeBrand" class="brand-tabs">
            <el-tab-pane
              v-for="(products, brand) in groupedProducts"
              :key="brand"
              :label="brand"
              :name="brand"
            >
              <!-- 缩短后的商品网格 -->
              <div class="product-grid">
                <div
                  v-for="p in products"
                  :key="p.id"
                  class="product-item"
                  :class="{ 'out-of-stock': p.stock <= 0 }"
                  @click="addToCart(p)"
                >
                  <div class="p-model">{{ p.model }}</div>
                  <div class="p-price">¥{{ p.sellingPrice }}</div>
                  <div class="p-stock">库存: {{ p.stock }}</div>
                  <div class="add-icon">
                    <el-icon><Plus /></el-icon>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>

          <el-empty
            v-if="Object.keys(groupedProducts).length === 0"
            description="暂无匹配商品"
          />
        </el-card>
      </el-col>

      <!-- 右侧：购物车区 (保持不变，略作美化) -->
      <el-col :span="10">
        <el-card class="cart-area" shadow="never">
          <template #header>
            <span class="title">🛒 销售清单</span>
          </template>

          <el-table :data="cartList" height="350" stripe size="small">
            <el-table-column label="型号">
              <template #default="scope"
                >{{ scope.row.brand }} {{ scope.row.model }}</template
              >
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="scope">
                <el-input-number
                  v-model="scope.row.quantity"
                  :min="1"
                  :max="scope.row.maxStock"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="90">
              <template #default="scope">
                <span class="item-total"
                  >¥{{ scope.row.quantity * scope.row.sellingPrice }}</span
                >
              </template>
            </el-table-column>
            <el-table-column width="50">
              <template #default="scope">
                <el-button
                  type="danger"
                  icon="Delete"
                  link
                  @click="removeFromCart(scope.$index)"
                />
              </template>
            </el-table-column>
          </el-table>

          <div class="checkout-area">
            <div class="total-price">
              总金额：<span>¥{{ totalAmount }}</span>
            </div>
            <el-form label-width="70px" size="default">
              <el-form-item label="客户">
                <el-select
                  v-model="customerId"
                  placeholder="选择客户"
                  filterable
                  style="width: 100%"
                >
                  <el-option
                    v-for="c in customerList"
                    :key="c.id"
                    :label="c.name"
                    :value="c.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="支付">
                <el-radio-group v-model="payStatus">
                  <el-radio label="paid">现金/微信</el-radio>
                  <el-radio label="debt">挂账(未付)</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-button
                type="primary"
                size="large"
                style="width: 100%; margin-top: 10px"
                @click="handleSubmit"
                :loading="submitting"
              >
                确认开单并扣库存
              </el-button>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { getProductList } from "@/api/product";
import { getCustomerList } from "@/api/customer";
import { submitOrder } from "@/api/orders";
import { ElMessage } from "element-plus";

const productList = ref([]);
const customerList = ref([]);
const cartList = ref([]);
const searchText = ref("");
const customerId = ref(null);
const payStatus = ref("paid");
const submitting = ref(false);
const activeBrand = ref("");

// 核心逻辑：将商品列表按品牌分组
const groupedProducts = computed(() => {
  const groups = {};
  productList.value.forEach((p) => {
    // 如果有搜索词，过滤掉不匹配的型号
    if (
      searchText.value &&
      !p.model.toLowerCase().includes(searchText.value.toLowerCase())
    ) {
      return;
    }
    if (!groups[p.brand]) {
      groups[p.brand] = [];
    }
    groups[p.brand].push(p);
  });
  return groups;
});

// 监听分组变化，默认选中第一个品牌
watch(groupedProducts, (newVal) => {
  if (!activeBrand.value && Object.keys(newVal).length > 0) {
    activeBrand.value = Object.keys(newVal)[0];
  }
});

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.quantity * item.sellingPrice, 0);
});

const loadProducts = () => {
  getProductList({ page: 1, size: 500 }).then((res) => {
    productList.value = res.data.list;
  });
};

const loadCustomers = () => {
  getCustomerList({ page: 1, size: 500 }).then((res) => {
    customerList.value = res.data.list;
  });
};

const addToCart = (product) => {
  if (product.stock <= 0) return ElMessage.warning("该型号暂时缺货");
  const existItem = cartList.value.find((item) => item.id === product.id);
  if (existItem) {
    if (existItem.quantity < product.stock) existItem.quantity++;
    else ElMessage.warning("超过库存上限");
  } else {
    cartList.value.push({
      id: product.id,
      brand: product.brand,
      model: product.model,
      sellingPrice: product.sellingPrice,
      quantity: 1,
      maxStock: product.stock,
    });
  }
};

const removeFromCart = (index) => cartList.value.splice(index, 1);

const handleSubmit = () => {
  if (cartList.value.length === 0) return ElMessage.warning("请先选择商品");
  if (!customerId.value) return ElMessage.warning("请选择客户");
  submitting.value = true;
  const data = {
    customerId: customerId.value,
    status: payStatus.value,
    items: cartList.value.map((item) => ({
      productId: item.id,
      quantity: item.quantity,
      price: item.sellingPrice,
    })),
  };
  submitOrder(data)
    .then(() => {
      ElMessage.success("开单成功！");
      cartList.value = [];
      loadProducts();
      submitting.value = false;
    })
    .catch(() => (submitting.value = false));
};

onMounted(() => {
  loadProducts();
  loadCustomers();
});
</script>

<style scoped>
.sales-container {
  height: calc(100vh - 120px);
}
.product-area,
.cart-area {
  height: 100%;
  overflow: hidden;
  border-radius: 8px;
}
.title {
  font-weight: bold;
  color: #333;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 品牌标签样式 */
.brand-tabs {
  margin-top: -10px;
}
:deep(.el-tabs__item) {
  font-weight: bold;
  font-size: 15px;
}

/* 重新设计的紧凑型卡片网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
  padding: 10px 5px;
  overflow-y: auto;
  max-height: 60vh;
}

.product-item {
  position: relative;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  /* 固定高度，解决“面积太长”的问题 */
  height: 110px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.p-model {
  font-weight: bold;
  font-size: 15px;
  color: #2c3e50;
  margin-bottom: 5px;
}
.p-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}
.p-stock {
  font-size: 12px;
  color: #909399;
}

.add-icon {
  position: absolute;
  right: 8px;
  bottom: 8px;
  color: #409eff;
  opacity: 0;
  transition: opacity 0.2s;
}
.product-item:hover .add-icon {
  opacity: 1;
}

.out-of-stock {
  background: #f5f7fa;
  opacity: 0.6;
  cursor: not-allowed;
}

/* 结算区 */
.checkout-area {
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
  margin-top: 10px;
}
.total-price {
  text-align: right;
  font-size: 16px;
}
.total-price span {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
.item-total {
  color: #f56c6c;
  font-weight: bold;
}
</style>
