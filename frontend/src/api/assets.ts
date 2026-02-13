import client from './client'

export interface Asset {
  id?: number
  name: string
  type: string
  amount: number
  description?: string
  familyId?: number
  createdBy?: number
}

export const assetsApi = {
  list() {
    return client.get('/assets')
  },
  get(id: number) {
    return client.get(`/assets/${id}`)
  },
  create(data: Asset) {
    return client.post('/assets', data)
  },
  update(id: number, data: Asset) {
    return client.put(`/assets/${id}`, data)
  },
  delete(id: number) {
    return client.delete(`/assets/${id}`)
  }
}
