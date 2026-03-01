<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="https://element-plus.org/images/element-plus-logo.svg" alt="logo" style="width: 50px;">
        <h2>电瓶管家 Pro</h2>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" prefix-icon="User" />
        </el-form-item>
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
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <p>演示账号：admin</p>
        <p>演示密码：123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 这里为了简单，暂时写死账号密码。
      // 如果你想做真实的后端验证，可以以后再加。
      setTimeout(() => {
        if (form.username === 'admin' && form.password === '123456') {
          ElMessage.success('登录成功，欢迎回来！')
          router.push('/dashboard') // 跳转到仪表盘
        } else {
          ElMessage.error('账号或密码错误')
          loading.value = false
        }
      }, 1000)
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2d3a4b; /* 深色背景 */
  background-image: linear-gradient(135deg, #2d3a4b 0%, #1e222d 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 10px 0 0;
  color: #303133;
}

.tips {
  margin-top: 20px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>