// 路由配置文件：定义系统所有页面路由，并通过全局前置守卫做登录校验和角色权限控制
// meta 里几个约定：title 用于面包屑和浏览器标题；requiresAuth 表示是否需要登录（默认要登录）；
// roles 表示哪些角色能访问这个页面；hidden 表示不在侧边栏显示（比如订单详情页）
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    // 登录后的页面都挂在 layout 下面，这样所有业务页面都共用同一套侧边栏和顶部栏
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        // 所有角色都能看首页
        meta: { title: '首页', icon: 'Odometer', roles: [1, 2, 3, 4, 5] }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        // 用户管理只有系统管理员能进
        meta: { title: '用户管理', icon: 'User', roles: [1] }
      },
      {
        path: 'institution',
        name: 'Institution',
        component: () => import('@/views/institution/index.vue'),
        // 机构管理：系统管理员和县级管理员
        meta: { title: '医疗机构管理', icon: 'OfficeBuilding', roles: [1, 2] }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        // 药品分类维护：系统管理员和县级管理员
        meta: { title: '药品分类管理', icon: 'CollectionTag', roles: [1, 2] }
      },
      {
        path: 'drug',
        name: 'Drug',
        component: () => import('@/views/drug/index.vue'),
        // 药品维护：系统管理员和县级管理员
        meta: { title: '药品管理', icon: 'MedicineBox', roles: [1, 2] }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/index.vue'),
        // 库存：县乡村三级都要用，配送员不用
        meta: { title: '库存管理', icon: 'Goods', roles: [1, 2, 3, 4] }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        // 订单：县乡村三级都要用，配送员不用
        meta: { title: '订单管理', icon: 'Document', roles: [1, 2, 3, 4] }
      },
      {
        path: 'order/detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { title: '订单详情', hidden: true, roles: [1, 2, 3, 4] }
      },
      {
        path: 'delivery',
        name: 'Delivery',
        component: () => import('@/views/delivery/index.vue'),
        // 配送任务：县级要发货、乡级要转发、配送员要送货，村级收货在订单里看就行
        meta: { title: '配送任务管理', icon: 'Van', roles: [1, 2, 3, 5] }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  },
  {
    // 兜底路由：没匹配上的地址一律跳 404 页
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置路由守卫：每次跳转前都会先经过这里
// 检查顺序：1. 设置页面标题 → 2. 不需要登录的页面直接放行 → 3. 没登录的踢去登录页 → 4. 登录了再检查角色权限
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 县域医共体药品配送管理系统'
  }

  // 不需要登录的页面（登录页、404页）
  if (to.meta.requiresAuth === false) {
    // 如果已登录，访问登录页则跳转到首页（没必要再登录一次）
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 需要登录的页面：没登录就跳登录页，并把原本想去的地址记在 redirect 参数里，登录成功后能跳回来
  if (!userStore.isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 菜单级权限控制：路由上配了 roles 的话，当前用户角色不在里面就不让进
  // 这是毕业设计，权限只做到菜单级，没做按钮级（答辩时说清楚就行）
  if (to.meta.roles && to.meta.roles.length > 0) {
    const roleId = userStore.userInfo?.roleId
    if (!to.meta.roles.includes(roleId)) {
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
