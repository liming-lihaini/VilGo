import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')

  const login = (user, password) => {
    // 固定账号验证
    const users = {
      admin: { password: 'admin', name: '管理员' },
      guest: { password: 'guest', name: '访客' }
    }

    if (users[user] && users[user].password === password) {
      const userInfo = { username: user, name: users[user].name }
      token.value = 'logged-in'
      username.value = user
      localStorage.setItem('token', 'logged-in')
      localStorage.setItem('username', user)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      return { success: true }
    }
    return { success: false, message: '用户名或密码错误' }
  }

  const logout = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userInfo')
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return { token, username, login, logout, isLoggedIn }
})