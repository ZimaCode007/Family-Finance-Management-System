import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type LoginRequest, type RegisterRequest } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userId = ref<number | null>(Number(localStorage.getItem('userId')) || null)
  const username = ref<string | null>(localStorage.getItem('username'))
  const nickname = ref<string | null>(localStorage.getItem('nickname'))
  const role = ref<string | null>(localStorage.getItem('role'))
  const familyId = ref<number | null>(Number(localStorage.getItem('familyId')) || null)
  const familyName = ref<string | null>(localStorage.getItem('familyName'))

  function setAuth(data: {
    token: string
    userId: number
    username: string
    nickname: string
    role: string
    familyId: number | null
    familyName: string | null
  }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    nickname.value = data.nickname
    role.value = data.role
    familyId.value = data.familyId
    familyName.value = data.familyName

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname)
    localStorage.setItem('role', data.role)
    if (data.familyId) localStorage.setItem('familyId', String(data.familyId))
    if (data.familyName) localStorage.setItem('familyName', data.familyName)
  }

  async function login(request: LoginRequest) {
    const res = await authApi.login(request)
    setAuth(res.data.data)
  }

  async function register(request: RegisterRequest) {
    const res = await authApi.register(request)
    setAuth(res.data.data)
  }

  function logout() {
    token.value = null
    userId.value = null
    username.value = null
    nickname.value = null
    role.value = null
    familyId.value = null
    familyName.value = null
    localStorage.clear()
  }

  const isLoggedIn = () => !!token.value

  return {
    token, userId, username, nickname, role, familyId, familyName,
    login, register, logout, isLoggedIn
  }
})
