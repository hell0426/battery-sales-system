<template>
  <el-container style="height: 100vh;">
    <!-- 左侧侧边栏 -->
    <el-aside width="220px" class="aside-menu">
      <!-- Logo 区域 -->
      <div class="logo">
        <h2>电瓶管家 Pro</h2>
        <span class="sub-logo">小微汽修管理系统</span>
      </div>

      <!-- 菜单项 -->
      <el-menu active-text-color="#409eff" background-color="#1e222d" class="el-menu-vertical" text-color="#a3a6af"
        :default-active="$route.path" router>
        <el-menu-item index="/dashboard">
          <el-icon>
            <DataBoard />
          </el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon>
            <Box />
          </el-icon>
          <span>电瓶库存</span>
        </el-menu-item>
        <el-menu-item index="/sales">
          <el-icon>
            <ShoppingCart />
          </el-icon>
          <span>开单销售</span>
        </el-menu-item>
        <el-menu-item index="/customer">
          <el-icon>
            <User />
          </el-icon>
          <span>客户管理</span>
        </el-menu-item>
        <el-menu-item index="/settlement">
          <el-icon>
            <Notebook />
          </el-icon>
          <span>月结对账</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶部 Header (可选，比如放个退出登录) -->
      <el-header class="main-header">
        <div class="header-right">
          <el-button type="danger" text icon="SwitchButton">退出登录</el-button>
        </div>
      </el-header>

      <!-- 核心页面显示区：点击左边菜单，这里的内容会变 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 不需要写什么逻辑，单纯的页面骨架
</script>

<style scoped>
/* 左侧菜单整体样式 */
.aside-menu {
  background-color: #1e222d;
  /* 深色背景 */
  display: flex;
  flex-direction: column;
}

/* 隐藏 Element 菜单自带的边框 */
.el-menu-vertical {
  border-right: none;
}

/* Logo 区域样式 */
.logo {
  height: 80px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  color: #66b1ff;
  /* 标题蓝色 */
}

.sub-logo {
  font-size: 12px;
  color: #a3a6af;
  margin-top: 5px;
}

/* 顶部栏样式 */
.main-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  /* 靠右对齐 */
}

/* 主内容区背景 */
.main-content {
  background-color: #f3f4f6;
  padding: 20px;
}
</style>