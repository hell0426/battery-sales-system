<template>
  <!-- 路由出口：页面会在这里显示 -->
  <router-view />
</template>

<script setup>
// 这里暂时什么都不用写
</script>

<style>
/* 全局基础样式，去掉白边，设置背景色 */
body {
  margin: 0;
  padding: 0;
  background-color: #f3f4f6;
  /* 你之前截图里的浅灰色背景 */
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}
</style>