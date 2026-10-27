// 用户状态管理（Pinia）：保存 token 和用户信息
// 同时往 localStorage 里写一份，这样刷新页面后登录状态不会丢（pinia 是内存存储，一刷新就没了）
import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  // 初始化时先从 localStorage 读，读不到就是未登录状态
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    // 有没有 token 就算有没有登录，简单直接
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.realName || ''
  },

  actions: {
    // 登录：调后端接口，成功后把 token 和用户信息同时存到 pinia 和 localStorage
    async login(loginForm) {
      const res = await login(loginForm)
      if (res.code === 200) {
        this.token = res.data.token
        this.userInfo = res.data.userInfo
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      }
      return res
    },

    // 获取用户信息（比如刷新后想拿最新的用户信息时用）
    async getUserInfo() {
      const res = await getUserInfo()
      if (res.code === 200) {
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
      return res
    },

    // 登出：调后端登出接口，然后清空本地所有登录状态
    async logout() {
      try {
        await logout()
      } catch (e) {
        // 就算后端接口失败（比如 token 已过期），本地状态也要清掉，不然用户会一直卡在已登录的假象里
        console.error('登出请求失败，但仍清除本地状态')
      }
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
