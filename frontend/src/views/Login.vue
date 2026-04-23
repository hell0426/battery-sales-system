<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <!-- Element Plus Logo 装饰 -->
        <img
          src="https://element-plus.org/images/element-plus-logo.svg"
          alt="logo"
          style="width: 50px"
        />
        <h2>电瓶管家 Pro</h2>
        <p class="sub-title">分级管理系统入口</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large">
        <!-- 1. 用户选择下拉框 -->
        <el-form-item prop="username">
          <el-select
            v-model="form.username"
            placeholder="请选择您的姓名"
            style="width: 100%"
            :loading="listLoading"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.realName"
              :value="user.username"
            >
              <span style="float: left">{{ user.realName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ user.role === "admin" ? "老板" : "员工" }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 2. 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <!-- 3. 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            立即登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>※ 忘记密码请联系管理员重置</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { login, getUserList } from "@/api/user"; // 确保你创建了 src/api/user.js

const router = useRouter();
const loading = ref(false);
const listLoading = ref(false);
const formRef = ref(null);

// 员工列表数据
const userList = ref([]);

// 表单数据
const form = reactive({
  username: "",
  password: "",
});

// 表单校验规则
const rules = {
  username: [{ required: true, message: "请选择您的身份", trigger: "change" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

// 页面初始化：获取所有员工列表
const fetchUserList = () => {
  listLoading.value = true;
  getUserList()
    .then((res) => {
      // 假设后端返回的数据在 res.data.items
      userList.value = res.data.items;
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
};

// 登录逻辑处理
const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;

      // 调用后端登录接口
      login(form)
        .then((res) => {
          const userInfo = res.data.userInfo;
          ElMessage.success(res.msg || "登录成功");

          //存入本地缓存，供 Layout 和权限校验使用
          localStorage.setItem("userRole", userInfo.role);
          localStorage.setItem("realName", userInfo.realName);
          localStorage.setItem("username", userInfo.username);

          // 根据角色进行路由跳转
          if (userInfo.role === "admin") {
            router.push("/dashboard"); // 老板跳转到仪表盘
          } else {
            router.push("/sales"); // 员工跳转到开单页
          }
        })
        .catch((err) => {
          console.error(err);
          loading.value = false;
        });
    }
  });
};

onMounted(() => {
  fetchUserList();
});
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 炫酷的深色背景 */
  background: #1e222d;
  background-image: linear-gradient(135deg, #2d3a4b 0%, #1e222d 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  margin: 15px 0 5px;
  color: #303133;
  font-size: 24px;
  letter-spacing: 2px;
}

.sub-title {
  font-size: 13px;
  color: #909399;
}

.login-footer {
  margin-top: 30px;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #c0c4cc;
}

/* 下单下拉框的一些样式调整 */
:deep(.el-select .el-input__wrapper) {
  padding-left: 10px;
}
</style>
