import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes:[
    {
      path: '/',
      redirect: '/dashboard', // 改成默认跳到仪表盘
      name: 'Layout',
      component: Layout,
      children:[
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: '/product',
          name: 'Product',
          component: () => import('../views/Product.vue') // 懒加载我们的电瓶页面
        },
        {
          path: '/customer',
          name: 'Customer',
          component: () => import('../views/Customer.vue')
        },
        {
          path: '/sales',
          name: 'Sales',
          component: () => import('../views/Sales.vue')
        },
        {
          path: '/settlement',
          name: 'Settlement',
          component: () => import('../views/Settlement.vue')
        },
        
      ]
    }
  ]
})

export default router