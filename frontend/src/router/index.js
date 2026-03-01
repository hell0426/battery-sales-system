import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 1. 登录页 (单独的路由，不套在 Layout 里)
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    
    // 2. 主框架
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: '/product',
          name: 'Product',
          component: () => import('../views/Product.vue')
        },
        {
          path: '/sales',
          name: 'Sales',
          component: () => import('../views/Sales.vue')
        },
        {
          path: '/customer',
          name: 'Customer',
          component: () => import('../views/Customer.vue')
        },
        {
          path: '/settlement',
          name: 'Settlement',
          component: () => import('../views/Settlement.vue')
        }
      ]
    }
  ]
})

// 简单路由守卫：没登录跳回登录页 (简单模拟)
router.beforeEach((to, from, next) => {
  // 如果去的是登录页，直接放行
  if (to.path === '/login') return next()
  
  // 这里为了毕设演示简单，我们就不做复杂的 Token 校验了
  // 直接放行，默认跳到登录页
  next() 
})

export default router