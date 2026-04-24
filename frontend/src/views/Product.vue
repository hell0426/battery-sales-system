<template>
  <div class="product-container">
    <!-- 1. 顶部搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item prop="brand" label="品牌">
          <!-- 搜索用的品牌下拉 -->
          <el-select
            v-model="queryForm.brand"
            placeholder="选择品牌"
            clearable
            style="width: 200px"
            @change="handleSearchBrandChange"
          >
            <el-option
              v-for="item in brandOptions"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>

        <el-form-item prop="modelId" label="型号">
          <!-- 搜索用的型号下拉 -->
          <el-select
            v-model="queryForm.model"
            placeholder="选择型号"
            clearable
            style="width: 200px"
            :disabled="!searchBrandId"
            @change="loadData"
          >
            <el-option
              v-for="item in searchModelOptions"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button
            v-if="userRole === 'admin'"
            type="success"
            icon="Plus"
            @click="handleAdd"
            >新增电瓶</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 表格区域 -->
    <el-card class="table-card">
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="brandName" label="品牌" width="120" />
        <el-table-column prop="modelName" label="型号" width="150" />
        <el-table-column v-if="userRole === 'admin'" prop="costPrice" label="进价(元)" />
        <el-table-column prop="sellingPrice" label="售价(元)">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold"
              >¥{{ scope.row.sellingPrice }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存">
          <template #default="scope">
            <el-tag :type="scope.row.stock < 10 ? 'danger' : 'success'"
              >{{ scope.row.stock }} 个</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="录入时间" width="180" />
        <el-table-column v-if="userRole === 'admin'" label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button
              type="danger"
              link
              icon="Delete"
              @click="handleDelete(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
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

    <!-- 3. 新增/编辑 弹窗 (补全了这部分) -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="品牌" prop="brand">
          <el-select
            v-model="tempBrandId"
            placeholder="请选择品牌"
            style="width: 100%"
            @change="handleDialogBrandChange"
          >
            <el-option
              v-for="item in brandOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="modelId">
          <el-select
            v-model="form.modelId"
            placeholder="请选择型号"
            :disabled="!tempBrandId"
            style="width: 100%"
          >
            <el-option
              v-for="item in dialogModelOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="进价" prop="costPrice">
          <el-input-number v-model="form.costPrice" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="售价" prop="sellingPrice">
          <el-input-number v-model="form.sellingPrice" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getProductList, addProduct, updateProduct, deleteProduct } from "@/api/product";
import { getBrandList, getModelsByBrand } from "@/api/category"; // 全部放在顶部
import { ElMessage, ElMessageBox } from "element-plus";

// --- 数据定义 ---
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref(null);

const brandOptions = ref([]); // 全局品牌列表
const searchModelOptions = ref([]); // 搜索栏专用型号列表
const dialogModelOptions = ref([]); // 弹窗专用型号列表
const searchBrandId = ref(null); // 搜索栏选中的品牌ID
const tempBrandId = ref(null); // 弹窗选中的品牌ID

const queryForm = reactive({ page: 1, size: 10, brand: "", model: "" });
const form = reactive({
  id: null,
  brand: "",
  model: "",
  modelId: null,
  costPrice: 0,
  sellingPrice: 0,
  stock: 0,
});

const rules = {
  brand: [{ required: true, message: "请选择品牌", trigger: "change" }],
  modelId: [{ required: true, message: "请选择型号", trigger: "change" }],
};

// 拿到当前登录人的角色
const userRole = localStorage.getItem("userRole");
// --- 逻辑处理 ---

// 1. 加载主列表
const loadData = () => {
  loading.value = true;
  getProductList(queryForm)
    .then((res) => {
      tableData.value = res.data.list;
      total.value = res.data.total;
      loading.value = false;
    })
    .catch(() => (loading.value = false));
};

// 2. 加载基础品牌
const loadBrands = () => {
  getBrandList().then((res) => {
    brandOptions.value = res.data.items;
  });
};

// 3. 搜索栏联动：选品牌后加载型号
const handleSearchBrandChange = (brandName) => {
  queryForm.model = "";
  const brand = brandOptions.value.find((b) => b.name === brandName);
  if (brand) {
    searchBrandId.value = brand.id;
    getModelsByBrand(brand.id).then((res) => {
      searchModelOptions.value = res.data.items;
    });
  } else {
    searchBrandId.value = null;
    searchModelOptions.value = [];
  }
  loadData();
};

// 4. 弹窗联动：选品牌后加载型号
const handleDialogBrandChange = (brandId) => {
  const brand = brandOptions.value.find((b) => b.id === brandId);
  if (brand) form.brand = brand.name;
  form.model = "";
  getModelsByBrand(brandId).then((res) => {
    dialogModelOptions.value = res.data.items;
  });
};

// 5. 重置搜索
const resetQuery = () => {
  queryForm.brand = "";
  queryForm.model = "";
  searchBrandId.value = null;
  searchModelOptions.value = [];
  loadData();
};

// 6. 新增/编辑逻辑
const handleAdd = () => {
  dialogTitle.value = "新增入库";
  Object.assign(form, {
    id: null,
    brand: "",
    model: "",
    costPrice: 0,
    sellingPrice: 0,
    stock: 0,
  });
  tempBrandId.value = null;
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  dialogTitle.value = "编辑电瓶";
  Object.assign(form, row);
  // 回显时直接使用后端传回来的 modelId
  tempBrandId.value = null; // 这里的逻辑可以优化为先去查型号属于哪个品牌，或者让后端直接带回 brandId
  const brand = brandOptions.value.find((b) => b.name === row.brand);
  if (brand) {
    tempBrandId.value = brand.id;
    getModelsByBrand(brand.id).then((res) => {
      dialogModelOptions.value = res.data.items;
    });
  }
  dialogVisible.value = true;
};

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const api = form.id ? updateProduct : addProduct;
      api(form).then(() => {
        ElMessage.success("操作成功");
        dialogVisible.value = false;
        loadData();
      });
    }
  });
};

const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除吗？", "提示", { type: "warning" }).then(() => {
    deleteProduct(id).then(() => {
      ElMessage.success("删除成功");
      loadData();
    });
  });
};
// 必须加上这个函数，否则页面会报错
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  tempBrandId.value = null;
  dialogModelOptions.value = [];
};
onMounted(() => {
  loadBrands();
  loadData();
});
</script>
