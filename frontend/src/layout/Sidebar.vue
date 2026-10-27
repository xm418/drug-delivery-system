<template>
  <div class="sidebar-wrapper">
    <!-- Logo -->
    <div class="logo">
      <img src="/logo.svg" alt="logo" class="logo-img" v-if="!isCollapse" />
      <img src="/logo.svg" alt="logo" class="logo-img-small" v-else />
      <h1 v-if="!isCollapse" class="logo-title">医共体配送系统</h1>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
      class="sidebar-menu"
    >
      <template v-for="item in menuList" :key="item.path">
        <el-menu-item :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
// 侧边栏组件：logo + 菜单
// 菜单不是写死的，而是根据当前登录用户的角色动态过滤，不同角色看到的菜单不一样
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

// isCollapse 由父组件 layout/index.vue 传进来，控制侧边栏折叠/展开
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前高亮的菜单项，直接用当前路由路径，el-menu 的 default-active 会匹配 index
const activeMenu = computed(() => route.path)

// 菜单和路由里的 roles 一一对应
// 1-系统管理员 2-县级管理员 3-乡级管理员 4-村级用户 5-配送员
const allMenus = [
  { path: '/dashboard', title: '首页', icon: 'Odometer', roles: [1, 2, 3, 4, 5] }, // 所有角色都有首页
  { path: '/user', title: '用户管理', icon: 'User', roles: [1] }, // 只有系统管理员能管用户
  { path: '/institution', title: '医疗机构管理', icon: 'OfficeBuilding', roles: [1, 2] }, // 系统管理员和县级管理员
  { path: '/category', title: '药品分类管理', icon: 'CollectionTag', roles: [1, 2] }, // 系统管理员和县级管理员
  { path: '/drug', title: '药品管理', icon: 'FirstAidKit', roles: [1, 2] }, // 系统管理员和县级管理员
  { path: '/inventory', title: '库存管理', icon: 'Goods', roles: [1, 2, 3, 4] }, // 县乡村三级都要查库存，配送员不用
  { path: '/order', title: '订单管理', icon: 'Document', roles: [1, 2, 3, 4] }, // 县乡村三级都涉及下单/审核，配送员不看订单
  { path: '/delivery', title: '配送任务管理', icon: 'Van', roles: [1, 2, 3, 5] } // 县级发货、乡级转发、配送员送货，村级收货在订单里看
]

// 按当前登录用户的角色过滤菜单，只显示他有权限的
// 这里用 computed 而不用 data，是因为角色信息变化时菜单要自动重新算
// （比如退出登录后 userInfo 变 null，菜单就清空了）
const menuList = computed(() => {
  // 用可选链防止 userInfo 还没加载出来时报错
  const roleId = userStore.userInfo?.roleId
  return allMenus.filter(item => item.roles.includes(roleId))
})
</script>

<style scoped lang="scss">
/* 侧边栏整体深色风格（#304156 是经典的深色后台配色），选中项用蓝色高亮 */
.sidebar-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4f;
  padding: 0 10px;
  overflow: hidden;

  .logo-img {
    width: 32px;
    height: 32px;
    margin-right: 10px;
  }

  .logo-img-small {
    width: 32px;
    height: 32px;
  }

  .logo-title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    white-space: nowrap;
    margin: 0;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none;

  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;

    &:hover {
      background-color: #263445 !important;
    }

    &.is-active {
      background-color: #409eff !important;
      color: #fff !important;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background-color: #fff;
      }
    }
  }

  :deep(.el-menu-item.is-active) {
    background-color: #409eff !important;
  }
}
</style>
