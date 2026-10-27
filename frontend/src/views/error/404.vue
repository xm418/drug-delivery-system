<template>
  <!-- 404 页面：路由里所有没匹配上的地址都会跳到这里 -->
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
// 404 页面：只提供一个"返回首页"按钮，跳回 dashboard
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style scoped lang="scss">
/* 简单居中大数字样式，灰背景配主题蓝 */
.not-found-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.not-found-content {
  text-align: center;

  .error-code {
    font-size: 120px;
    font-weight: bold;
    color: #409eff;
    margin: 0;
    line-height: 1;
  }

  .error-message {
    font-size: 18px;
    color: #606266;
    margin: 20px 0 30px;
  }
}
</style>
