<template>
  <!-- 根组件：整个应用的出口，只放一个 router-view，具体显示什么由路由决定 -->
  <!-- 登录页、布局页（内含各业务页面）、404页都是从这里渲染出来的 -->
  <router-view />
</template>

<script setup>
// 根组件不需要任何逻辑，所有功能都在各自的页面组件里
</script>

<style>
/* 全局样式：让 #app 撑满整个浏览器窗口，不限制的话子元素的 100vh 布局会有问题 */
#app {
  width: 100%;
  height: 100%;
}
</style>
