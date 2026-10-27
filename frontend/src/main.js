// 前端入口文件：创建 Vue 应用实例，注册各种插件（Pinia、Router、Element Plus），最后挂载到 index.html 的 #app 上
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import './assets/styles/global.scss'

const app = createApp(App)

// 注册所有 Element Plus 图标为全局组件，这样在模板里直接写 <Fold />、<User /> 就能用，不用每个文件都 import
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia()) // 状态管理
app.use(router) // 路由
app.use(ElementPlus, { locale: zhCn }) // UI 组件库，配中文语言包（分页、日期选择器等默认是英文的）

app.mount('#app')
