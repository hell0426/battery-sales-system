import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes:[
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/product', // 默认打开就跳到电瓶库存页
      children:[
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
      ]
    }
  ]
})

export default router