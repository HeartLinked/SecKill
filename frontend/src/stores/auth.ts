import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

interface User {
  username: string
  email: string
  mobile: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = ref(false)

  async function login(email: string, password: string) {
    try {
      const response = await axios.post('/api/login', { email, password })
      if (response.status === 200) {
        user.value = response.data.user
        isAuthenticated.value = true
        return true
      } else {
        throw new Error('登录失败')
      }
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message)
      }
      throw new Error('登录失败')
    }
  }

  async function register(userData: User & { password: string }) {
    try {
      const response = await axios.post('/api/register', userData)
      if (response.status === 200) {
        user.value = {
          username: userData.username,
          email: userData.email,
          mobile: userData.mobile
        }
        isAuthenticated.value = true
        return true
      } else {
        throw new Error('注册失败')
      }
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message)
      }
      throw new Error('注册失败')
    }
  }

  function logout() {
    user.value = null
    isAuthenticated.value = false
  }
  
  function setAuthenticated(value: boolean) {
    isAuthenticated.value = value
  }

  return { user, isAuthenticated, login, register, logout, setAuthenticated }
})