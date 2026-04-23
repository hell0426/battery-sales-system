<template>
  <div class="manage-container">
    <el-tabs type="border-card">
      <!-- 1. 品牌管理标签 -->
      <el-tab-pane label="品牌管理">
        <div style="margin-bottom: 20px">
          <el-button type="primary" @click="handleAddBrand">新增品牌</el-button>
        </div>
        <el-table :data="brandList" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="品牌名称" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button link type="primary" @click="handleEditBrand(scope.row)"
                >修改</el-button
              >
              <el-button link type="danger" @click="handleDelBrand(scope.row.id)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 2. 型号管理标签 -->
      <el-tab-pane label="型号配置">
        <div style="margin-bottom: 20px">
          <el-button type="primary" @click="handleAddModel">新增型号</el-button>
        </div>
        <el-table :data="modelList" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="所属品牌">
            <template #default="scope">
              {{ getBrandName(scope.row.brandId) }}
            </template>
          </el-table-column>
          <el-table-column prop="name" label="型号名称" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button link type="primary" @click="handleEditModel(scope.row)"
                >修改</el-button
              >
              <el-button link type="danger" @click="handleDelModel(scope.row.id)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 品牌弹窗 -->
    <el-dialog v-model="brandDialog" title="品牌操作" width="350px">
      <el-input v-model="brandForm.name" placeholder="品牌名称" />
      <template #footer>
        <el-button type="primary" @click="submitBrand">确定</el-button>
      </template>
    </el-dialog>

    <!-- 型号弹窗 -->
    <el-dialog v-model="modelDialog" title="型号操作" width="350px">
      <el-form label-position="top">
        <el-form-item label="选择品牌">
          <el-select v-model="modelForm.brandId" style="width: 100%">
            <el-option v-for="b in brandList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="型号名称">
          <el-input v-model="modelForm.name" placeholder="例如: 12V-100AH" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitModel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import {
  getBrandList,
  saveBrand,
  deleteBrand,
  getModels,
  saveModel,
  deleteModel,
} from "@/api/category";
import { ElMessage } from "element-plus";

const brandList = ref([]);
const modelList = ref([]);
const brandDialog = ref(false);
const modelDialog = ref(false);

const brandForm = reactive({ id: null, name: "" });
const modelForm = reactive({ id: null, brandId: null, name: "" });

const loadData = () => {
  getBrandList().then((res) => (brandList.value = res.data.items));
  getModels().then((res) => (modelList.value = res.data.items));
};

const getBrandName = (id) => {
  const brand = brandList.value.find((b) => b.id === id);
  return brand ? brand.name : "未知品牌";
};

const handleAddBrand = () => {
  brandForm.name = "";
  brandDialog.value = true;
};
const handleEditBrand = (row) => {
  // 把这一行的数据填进表单
  brandForm.id = row.id;
  brandForm.name = row.name;
  // 弹出同一个对话框
  brandDialog.value = true;
};
const submitBrand = () =>
  saveBrand(brandForm).then(() => {
    ElMessage.success("成功");
    brandDialog.value = false;
    loadData();
  });
const handleDelBrand = (id) =>
  deleteBrand(id).then(() => {
    ElMessage.success("已删除");
    loadData();
  });

const handleAddModel = () => {
  Object.assign(modelForm, { brandId: null, name: "" });
  modelDialog.value = true;
};
const handleEditModel = (row) => {
  // 把这一行的数据（ID、品牌ID、型号名）填进表单
  modelForm.id = row.id;
  modelForm.brandId = row.brandId;
  modelForm.name = row.name;
  // 弹出同一个对话框
  modelDialog.value = true;
};
const submitModel = () =>
  saveModel(modelForm).then(() => {
    ElMessage.success("成功");
    modelDialog.value = false;
    loadData();
  });
const handleDelModel = (id) =>
  deleteModel(id).then(() => {
    ElMessage.success("已删除");
    loadData();
  });

onMounted(loadData);
</script>
