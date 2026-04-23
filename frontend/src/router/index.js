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

// 路由守卫：没登录跳回登录页

router.beforeEach((to, from, next) => {
  const role = localStorage.getItem("userRole")
  
  // 1. 如果没登录且不是去登录页，强行跳到登录页
  if (!role && to.path !== '/login') {
    return next('/login')
  }

  // 2. 权限限制：如果是员工(staff)想去老板的页面(仪表盘/对账)
  const adminPages = ['/dashboard', '/settlement']
  if (role === 'staff' && adminPages.includes(to.path)) {
    return next('/product') // 员工乱闯就踢回库存页
  }

  next()
})
export default router