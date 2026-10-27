<template>
  <!-- 登录页容器，占满整个屏幕 -->
  <div class="login-container">
    <div class="login-box">
      <!-- 左边：系统介绍（蓝色渐变那块） -->
      <div class="login-left">
        <div class="login-left-content">
          <h1 class="system-title">县域医共体药品配送管理系统</h1>
          <!-- 原来这行是英文副标题，按要求只去掉英文、排版样式保持不变，故保留空行占位 -->
          <p class="system-subtitle"></p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Van /></el-icon>
              <span>统一配送管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Goods /></el-icon>
              <span>智能库存预警</span>
            </div>
            <div class="feature-item">
              <el-icon><DataLine /></el-icon>
              <span>数据统计分析</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右边：登录表单 -->
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="login-title">用户登录</h2>
          <!-- 登录表单 -->
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <!-- 用户名输入框 -->
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <!-- 密码输入框 -->
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <!-- 验证码功能暂时没做，先注释掉 -->
            <!-- <el-form-item prop="captcha">
              <el-input v-model="loginForm.captcha" placeholder="请输入验证码">
                <template #append>
                  <div class="captcha-img">验证码</div>
                </template>
              </el-input>
            </el-form-item> -->
            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
                <a href="#" class="forgot-password">忘记密码？</a>
              </div>
            </el-form-item>
            <!-- 登录按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          <!-- 演示账号：点一下自动填上，答辩演示的时候切换角色方便 -->
          <div class="login-tip">
            <p class="tip-title">演示账号（点击自动填充，密码均为 123456）</p>
            <div class="account-list">
              <span
                v-for="acc in demoAccounts"
                :key="acc.username"
                class="account-tag"
                @click="fillAccount(acc)"
              >{{ acc.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

// 路由和状态管理
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用，用来做校验
const loginFormRef = ref(null)
// 登录按钮的loading状态
const loading = ref(false)

// 登录表单数据
// 默认填好了admin，方便测试
const loginForm = reactive({
  username: 'admin',
  password: '123456',
  captcha: '',   // 验证码，暂时没用
  remember: true // 记住密码
})

// 演示账号列表，和数据库里的演示数据一一对应
const demoAccounts = [
  { label: '系统管理员', username: 'admin' },
  { label: '县级管理员', username: 'pharmacy01' },
  { label: '乡级管理员', username: 'township01' },
  { label: '村级用户', username: 'village01' },
  { label: '配送员', username: 'delivery01' }
]

// 点演示账号标签，自动填用户名密码
const fillAccount = (acc) => {
  loginForm.username = acc.username
  loginForm.password = '123456'
}

// 表单校验规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

/**
 * 登录方法
 * 1. 先校验表单
 * 2. 调用store的登录接口
 * 3. 登录成功跳转到首页或之前的页面
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单校验
    await loginFormRef.value.validate()
    loading.value = true
    
    // 调用登录接口
    const res = await userStore.login(loginForm)
    
    if (res.code === 200) {
      ElMessage.success('登录成功')
      // 跳转到之前想去的页面，没有就去首页
      const redirect = route.query.redirect || '/dashboard'
      router.push(redirect)
    }
  } catch (error) {
    // 错误信息在request.js的响应拦截器里已经弹了
    console.log('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 登录页容器：背景整张完整显示不裁切（contain），两侧空白用深蓝渐变补上 */
.login-container {
  width: 100%;
  height: 100vh;
  background-image: url('/login-bg.jpg'),
    linear-gradient(160deg, #0a1f3a 0%, #123861 100%);
  background-size: contain, cover;
  background-position: center center, center center;
  background-repeat: no-repeat, no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

/* 登录框整体 */
.login-box {
  width: 900px;
  height: 500px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  overflow: hidden;
}

/* 左侧介绍区域 */
.login-left {
  width: 55%;
  background: #417ff9;
  padding: 60px 40px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-left .system-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  line-height: 1.4;
}

.login-left .system-subtitle {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 50px;
}

.login-left .feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.login-left .feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.login-left .feature-item .el-icon {
  font-size: 24px;
}

/* 右侧登录表单区域 */
.login-right {
  width: 45%;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-right .login-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 30px;
  text-align: center;
}

.login-right .login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-right .login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-right .forgot-password {
  color: #409eff;
  font-size: 14px;
  text-decoration: none;
}

.login-right .forgot-password:hover {
  text-decoration: underline;
}

.login-right .login-tip {
  margin-top: 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}

/* 演示账号区域 */
.login-tip .tip-title {
  margin-bottom: 10px;
}

.login-tip .account-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.login-tip .account-tag {
  padding: 4px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  font-size: 12px;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.login-tip .account-tag:hover {
  color: #409eff;
  border-color: #409eff;
  background-color: #ecf5ff;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .login-box {
    width: 100%;
    max-width: 400px;
    flex-direction: column;
    height: auto;
  }
  .login-left {
    width: 100%;
    padding: 40px 30px;
  }
  .login-left .feature-list {
    display: none;
  }
  .login-left .system-subtitle {
    margin-bottom: 0;
  }
  .login-right {
    width: 100%;
    padding: 30px;
    box-sizing: border-box;
  }
}
</style>
