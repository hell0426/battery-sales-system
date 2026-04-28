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
        },
        { 
          path: '/user', 
          name: 'UserManage',
          component: () => import('../views/UserManage.vue') 
        },
        { 
          path: '/category', 
          name: 'ModelManage',
          component: () => import('../views/ModelManage.vue') 
        },
        {
          path: '/sales-stats',
          name: 'SalesStats',
          component: () => import('../views/SalesStats.vue')
        }
      ]
    }
  ]
})

// 路由守卫：没登录跳回登录页

router.beforeEach((to, from, next) => {
  const role = sessionStorage.getItem("userRole")
  
  // 1. 如果没登录且不是去登录页，强行跳到登录页
  if (!role && to.path !== '/login') {
    return next('/login')
  }

  // 2. 权限限制：真正的老板专属页面（店员绝对不能进的）
  const adminPages = [
    '/dashboard',    // 经营仪表盘（涉及全店利润，店员不能看）
    '/settlement',   // 月结对账（涉及钱款核销，店员不能看）
    '/user',         // 员工管理
    '/category'      // 品牌型号配置
  ]

  

  if (role === 'staff' && adminPages.includes(to.path)) {
    return next('/product') // 只有店员乱闯老板页面时才踢回库存页
  }

  // 3. 已登录状态下访问登录页，直接送回主页
  if (role && to.path === '/login') {
    return next(role === 'admin' ? '/dashboard' : '/product')
  }

  next()
})
export default router