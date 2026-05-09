<template>
  <div class="sales-container">
    <el-row :gutter="20" style="height: 100%">
      <!-- 左侧：商品选择区 -->
      <el-col :span="14">
        <el-card class="product-area" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="title">📦 商品选购</span>
              <el-input
                v-model="searchText"
                placeholder="搜索型号..."
                style="width: 200px"
                prefix-icon="Search"
                clearable
              />
            </div>
          </template>

          <!-- 品牌分类标签页：修改为 brandName -->
          <el-tabs v-model="activeBrand" class="brand-tabs">
            <el-tab-pane
              v-for="(products, brand) in groupedProducts"
              :key="brand"
              :label="brand"
              :name="brand"
            >
              <div class="product-grid">
                <div
                  v-for="p in products"
                  :key="p.id"
                  class="product-item"
                  :class="{ 'out-of-stock': p.stock <= 0 }"
                  @click="addToCart(p)"
                >
                  <!-- p.model 改为 p.modelName -->
                  <div class="p-model">{{ p.modelName }}</div>
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

      <!-- 右侧：购物车区 -->
      <el-col :span="10">
        <el-card class="cart-area" shadow="never">
          <template #header>
            <span class="title">🛒 销售清单</span>
          </template>

          <el-table :data="cartList" height="350" stripe size="small">
            <el-table-column label="型号">
              <!-- 改为显示 brandName 和 modelName -->
              <template #default="scope"
                >{{ scope.row.brandName }} {{ scope.row.modelName }}</template
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
            <!-- 四行金额展示：原价 → 尊享价 → 优惠金额 → 最终应收 -->
            <div class="total-price" style="font-size: 14px; color: #999">
              商品原价：<span>¥{{ totalAmount }}</span>
            </div>
            <div
              class="total-price"
              style="font-size: 14px; color: #e6a23c"
              v-if="customerDiscountRate < 1"
            >
              客户尊享价（{{ (customerDiscountRate * 100).toFixed(0) }}折）：
              <span>¥{{ (totalAmount * customerDiscountRate).toFixed(2) }}</span>
            </div>
            <div
              class="total-price"
              style="font-size: 14px; color: #f56c6c"
              v-if="customerDiscountRate < 1"
            >
              优惠金额：
              <span style="font-size: 18px"
                >-¥{{ discountAmount.toFixed(2) }}</span
              >
            </div>
            <!-- 最终实收显示 -->
            <div class="total-price">
              最终应收：<span style="color: #67c23a"
                >¥
                {{ (totalAmount * customerDiscountRate).toFixed(2) }}</span
              >
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
/**
 * Sales.vue —— 开单销售页面
 *
 * 核心流程：
 * 1. 选择商品 → 加入购物车（纯前端操作，不请求后端）
 * 2. 选择客户 → watch 自动读取客户折扣率 → computed 实时计算优惠价格
 * 3. 选择支付方式（paid 现结 / debt 挂账）
 * 4. 点击提交 → handleSubmit 打包数据 → submitOrder API → 后端 createOrder 事务处理
 *
 * 价格实时计算机制（纯前端，不调后端）：
 *   - cartList 变化 → totalAmount computed 自动重算
 *   - customerId 变化 → customerDiscountRate watch 自动更新
 *   - 任一变化 → 模板里的 {{ totalAmount * customerDiscountRate }} 自动刷新显示
 */
import { ref, computed, onMounted, watch } from "vue";
import { getProductList } from "@/api/product";
import { getCustomerList } from "@/api/customer";
import { submitOrder } from "@/api/orders";
import { ElMessage } from "element-plus";

// ---- 响应式状态变量 ----
const productList = ref([]);       // 商品列表（从后端加载）
const customerList = ref([]);      // 客户列表（从后端加载）
const cartList = ref([]);          // 购物车（纯前端内存，加商品时 push）
const searchText = ref("");        // 搜索关键字
const customerId = ref(null);      // 当前选中的客户 ID
const payStatus = ref("paid");     // 支付方式：paid（现结）或 debt（挂账）
const submitting = ref(false);     // 提交中状态（防止重复点击）
const activeBrand = ref("");       // 当前激活的品牌 Tab

/**
 * 客户折扣率 —— 默认 1.0 (无折扣)
 * 选择客户时由 watch 自动从 customerList 中提取
 */
const customerDiscountRate = ref(1);

// ==================== computed 计算属性 ====================
// computed 的核心作用：依赖的数据变了，它自动重算，无需手动刷新
// 类比 Excel 公式单元格 —— 改一个格子，所有关联公式自动更新

/**
 * 按品牌分组后的商品列表
 * 依赖：productList（商品数据）和 searchText（搜索关键字）
 * 任一变化 → 自动重新分组
 */
const groupedProducts = computed(() => {
  const groups = {};
  productList.value.forEach((p) => {
    // 搜索过滤：型号名包含关键字才显示
    if (
      searchText.value &&
      !p.modelName?.toLowerCase().includes(searchText.value.toLowerCase())
    ) {
      return;
    }
    const bName = p.brandName || "未知品牌";
    if (!groups[bName]) {
      groups[bName] = [];
    }
    groups[bName].push(p);
  });
  return groups;
});

// 当商品分组变化时，默认激活第一个品牌 Tab
watch(groupedProducts, (newVal) => {
  if (!activeBrand.value && Object.keys(newVal).length > 0) {
    activeBrand.value = Object.keys(newVal)[0];
  }
});

/**
 * 监听客户选择变化 → 自动提取该客户的折扣率
 *
 * 流程：用户下拉选择客户"老张"
 *   → watch 触发
 *   → 从 customerList 中找到老张的 discountRate = 0.9
 *   → customerDiscountRate 变为 0.9
 *   → 所有依赖它的 computed 自动重算 → 页面价格自动刷新
 */
watch(customerId, (newVal) => {
  if (newVal) {
    const customer = customerList.value.find((c) => c.id === newVal);
    customerDiscountRate.value = customer?.discountRate || 1;
  } else {
    customerDiscountRate.value = 1; // 清空客户时恢复原价
  }
});

/**
 * 购物车商品原价总和
 * 依赖：cartList（每次添加/删除商品自动重算）
 */
const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.quantity * item.sellingPrice, 0);
});

/**
 * 优惠减免金额 = 原价 × (1 - 折扣率)
 * 依赖：totalAmount（原价变了）和 customerDiscountRate（折扣变了）
 * 任一变 → 自动重算
 */
const discountAmount = computed(() => {
  return totalAmount.value * (1 - customerDiscountRate.value);
});

/**
 * 加载商品列表
 * 调用 getProductList API → axios GET/POST → Vite 代理转发 → ProductController → MyBatis-Plus 联表查询
 * 返回的 JSON 经过 axios 自动 JSON.parse() 变成 JS 对象，存入 productList
 */
const loadProducts = () => {
  getProductList({ page: 1, size: 500 }).then((res) => {
    productList.value = res.data.list; // res 已经是 JS 对象，可 .data.list 访问
  });
};

/**
 * 加载客户列表（含 discount_rate 折扣率字段）
 */
const loadCustomers = () => {
  getCustomerList({ page: 1, size: 500 }).then((res) => {
    customerList.value = res.data.list;
  });
};

/**
 * 将商品加入购物车（纯前端操作，不调后端）
 * @param {Object} product - 点击的商品对象
 */
const addToCart = (product) => {
  if (product.stock <= 0) return ElMessage.warning("该型号暂时缺货");
  const existItem = cartList.value.find((item) => item.id === product.id);
  if (existItem) {
    if (existItem.quantity < product.stock) existItem.quantity++;
    else ElMessage.warning("超过库存上限");
  } else {
    cartList.value.push({
      id: product.id,
      brandName: product.brandName,
      modelName: product.modelName,
      sellingPrice: product.sellingPrice,
      quantity: 1,
      maxStock: product.stock, // 记录库存上限，用于前端校验
    });
  }
};

const removeFromCart = (index) => cartList.value.splice(index, 1);

/**
 * 提交开单 —— 从前端到后端的完整调用链入口
 *
 * 完整链路：
 * 1. 前端校验（购物车非空、已选客户）
 * 2. 组装 OrderSubmitVo 数据（customerId, userId, status, items）
 * 3. 调用 submitOrder(data) → api/orders.js → axios POST /api/orders/submit
 * 4. Vite 代理转发到 localhost:8080/orders/submit
 * 5. OrdersController.submitOrder() 接收 → ordersService.createOrder(vo)
 * 6. 后端事务：校验库存 → 扣库存 → 算折扣 → INSERT orders → INSERT order_item
 * 7. 成功：清空购物车、重置折扣率、刷新库存
 * 8. 失败：后端抛异常（库存不足等），axios 响应拦截器弹错误提示
 */
const handleSubmit = () => {
  // 前端表单校验（提升用户体验，后端还有更严谨的校验）
  if (cartList.value.length === 0) return ElMessage.warning("请先选择商品");
  if (!customerId.value) return ElMessage.warning("请选择客户");

  submitting.value = true; // 按钮显示 loading 转圈，防止重复提交

  // 组装提交数据（对应后端 OrderSubmitVo）
  const data = {
    customerId: customerId.value,                       // 客户 ID
    userId: sessionStorage.getItem("userId"),            // 当前登录员工 ID
    status: payStatus.value,                            // paid（现结）或 debt（挂账）
    items: cartList.value.map((item) => ({              // 购物车商品列表
      productId: item.id,
      quantity: item.quantity,
      price: item.sellingPrice,
    })),
  };

  /**
   * submitOrder(data) 返回 Promise
   * .then() —— 后端返回 RespBean(code=200) 时触发
   * .catch() —— 后端异常 / 网络错误时触发
   */
  submitOrder(data)
    .then(() => {
      ElMessage.success("开单成功！");
      cartList.value = [];                // 清空购物车
      customerDiscountRate.value = 1;     // 重置折扣率为 1（无折扣）
      loadProducts();                     // 重新加载商品列表（刷新库存显示）
      submitting.value = false;
    })
    .catch(() => (submitting.value = false)); // 失败时也要恢复按钮状态
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
.brand-tabs {
  margin-top: -10px;
}
:deep(.el-tabs__item) {
  font-weight: bold;
  font-size: 15px;
}
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
