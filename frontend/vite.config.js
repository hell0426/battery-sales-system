import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
 
  server: {
    port: 5173, // 前端固定运行在 5173 端口
    proxy: {
      '/api': { // 只要看到请求是以 /api 开头的
        target: 'http://localhost:8080', // 就自动转发给后端的 8080 端口
        changeOrigin: true, // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 关键动作：把路径里的 /api 去掉，发给后端真实的 /product/list
      }
    }
  }
})