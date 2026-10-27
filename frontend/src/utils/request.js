// axios 的统一封装：所有后端请求都走这里发出去
// 主要做三件事：1. 统一加 baseURL 和超时时间 2. 请求时自动带上 token 3. 响应时统一处理错误（特别是 401 跳登录）
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建 axios 实例，baseURL 配成 /api，开发环境会被 vite 的 proxy 转发到后端
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：每次发请求前都会经过这里
service.interceptors.request.use(
  (config) => {
    // 从 pinia 里拿 token，有的话就塞到请求头里，后端靠这个识别是谁在操作
    // 注意：要在拦截器里调 useUserStore()，不能在文件顶部调，那时候 pinia 还没初始化
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    return config
  },
  (error) => {
    // 这里一般很少进来，主要是请求还没发出去就出错了（比如参数问题）
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器：后端返回的数据先经过这里，统一处理后页面里拿到的就是干净的数据了
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 根据后端约定的状态码判断（200 和 0 都算成功，后端两种都返回过）
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未登录或token过期
      // token过期后端会返回401，收到401就清掉本地token跳回登录页，让用户重新登录
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      // 成功的话直接把 data 部分返回，页面里就不用再 .data 一层了
      return res
    }
  },
  (error) => {
    // 走到这里说明是 HTTP 层面的错误（网络断了、或者后端返回了 4xx/5xx 状态码）
    console.error('响应错误:', error)

    if (error.response) {
      // 有 response 说明后端收到了请求并返回了错误状态码，按状态码分别提示
      switch (error.response.status) {
        case 401:
          // 和上面一样，401 就清登录状态跳登录页
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || error.message)
      }
    } else {
      // 没有 response 说明请求根本没到后端，一般是网络问题
      ElMessage.error('网络错误，请检查网络连接')
    }

    // 继续 reject，这样页面里的 catch 还能捕获到做进一步处理（比如关掉 loading）
    return Promise.reject(error)
  }
)

export default service
