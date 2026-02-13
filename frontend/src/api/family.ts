import client from './client'

export interface Family {
  id: number
  name: string
  description: string | null
}

export interface FamilyMember {
  id: number
  username: string
  nickname: string
  role: string
}

export const familyApi = {
  getFamily(id: number) {
    return client.get(`/families/${id}`)
  },
  updateFamily(id: number, data: Partial<Family>) {
    return client.put(`/families/${id}`, data)
  },
  getMembers(id: number) {
    return client.get(`/families/${id}/members`)
  },
  addMember(id: number, username: string) {
    return client.post(`/families/${id}/members`, { username })
  },
  removeMember(familyId: number, userId: number) {
    return client.delete(`/families/${familyId}/members/${userId}`)
  }
}
