/**
 * Vue Router 路由配置 + 前端权限守卫
 *
 * 角色体系：
 *   - admin（老板）：可访问所有页面
 *   - staff（店员）：仅可访问 /product、/sales、/customer、/sales-stats
 *
 * 路由懒加载：
 *   所有页面组件使用 () => import('...') 动态导入，
 *   用户访问时才加载对应 JS，减少首屏加载时间
 *
 * ⚠️ 安全警告：
 *   路由守卫只在浏览器端生效，本质是"用户体验"而不是真正的安全。
 *   店员用 Postman 绕过浏览器直接调后端接口，路由守卫完全拦不住。
 *   真正的安全应该由后端 Spring Security + JWT 实现。
 */
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 1. 登录页 —— 独立路由，不套在 Layout 布局里
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },

    // 2. 主框架 —— 所有需要侧边栏+顶栏的页面都是它的 children
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/dashboard', // 默认跳仪表盘（老板）或库存（店员，由守卫处理）
      children: [
        // ===== 老板专属页面 =====
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('../views/Dashboard.vue')
        },
        // ===== 店员可访问页面 =====
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
        // ===== 老板专属页面 =====
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
        // ===== 店员可访问页面 =====
        {
          path: '/sales-stats',
          name: 'SalesStats',
          component: () => import('../views/SalesStats.vue')
        }
      ]
    }
  ]
})

/**
 * 全局路由守卫 —— 每次路由跳转时都会执行
 *
 * 参数说明：
 *   to   = 要去的页面（目标路由）
 *   from = 从哪个页面来（来源路由）
 *   next = 放行函数，调用 next() 才允许跳转
 *
 * 三条规则（按优先级）：
 *   ① 未登录 + 不是去登录页 → 强制跳回 /login
 *   ② staff 角色试图访问老板页面 → 强制跳回 /product
 *   ③ 已登录用户访问 /login → 直接跳到对应主页
 */
router.beforeEach((to, from, next) => {
  // 从 sessionStorage 读取登录时保存的角色信息
  const role = sessionStorage.getItem("userRole")

  // 规则①：没有登录（没有 role）且不是去登录页 → 踢回登录页
  if (!role && to.path !== '/login') {
    return next('/login')
  }

  // 规则②：店员权限限制
  // 这四个页面涉及经营数据、钱款核销、员工管理，店员无权访问
  const adminPages = [
    '/dashboard',    // 经营仪表盘（涉及全店利润，店员不能看）
    '/settlement',   // 月结对账（涉及钱款核销，店员不能看）
    '/user',         // 员工管理
    '/category'      // 品牌型号配置
  ]

  if (role === 'staff' && adminPages.includes(to.path)) {
    // 店员乱闯老板页面 → 踢回库存页，不给任何提示（避免触发好奇心）
    return next('/product')
  }

  // 规则③：已经登录了还去登录页 → 根据角色送去对应主页
  if (role && to.path === '/login') {
    return next(role === 'admin' ? '/dashboard' : '/product')
  }

  // 一切正常 → 放行
  next()
})

export default router
