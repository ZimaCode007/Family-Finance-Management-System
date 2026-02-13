import client from './client'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname?: string
  familyName?: string
  familyId?: number
}

export interface AuthResponse {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
  familyId: number | null
  familyName: string | null
}

export const authApi = {
  login(data: LoginRequest) {
    return client.post<{ success: boolean; message: string; data: AuthResponse }>('/auth/login', data)
  },
  register(data: RegisterRequest) {
    return client.post<{ success: boolean; message: string; data: AuthResponse }>('/auth/register', data)
  }
}
