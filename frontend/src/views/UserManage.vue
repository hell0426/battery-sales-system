<template>
  <div class="manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button type="primary" icon="Plus" @click="handleAdd">新增员工</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="realName" label="员工姓名" />
        <el-table-column prop="username" label="登录账号" />
        <el-table-column prop="role" label="身份角色">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
              {{ scope.row.role === "admin" ? "老板" : "店员" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入职时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">修改</el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(scope.row.id)"
              :disabled="scope.row.username === 'admin'"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑员工' : '新增员工'"
      width="400px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="员工真实姓名" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="重置或新建密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="店员" value="staff" />
            <el-option label="老板" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { getUserList, saveUser, deleteUser } from "@/api/user";
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);
const tableData = ref([]);
const dialogVisible = ref(false);
const form = reactive({
  id: null,
  username: "",
  password: "",
  realName: "",
  role: "staff",
});

const loadData = () => {
  loading.value = true;
  getUserList().then((res) => {
    tableData.value = res.data.items;
    loading.value = false;
  });
};

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    username: "",
    password: "",
    realName: "",
    role: "staff",
  });
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  Object.assign(form, row);
  dialogVisible.value = true;
};

const submitForm = () => {
  saveUser(form).then(() => {
    ElMessage.success("操作成功");
    dialogVisible.value = false;
    loadData();
  });
};

const handleDelete = (id) => {
  ElMessageBox.confirm("确定删除该员工账号吗？", "提示").then(() => {
    deleteUser(id).then(() => {
      ElMessage.success("删除成功");
      loadData();
    });
  });
};

onMounted(loadData);
</script>
