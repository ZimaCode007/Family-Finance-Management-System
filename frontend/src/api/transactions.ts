import client from './client'

export interface Transaction {
  id?: number
  type: string
  amount: number
  category?: string
  description?: string
  transactionDate: string
  assetId?: number
  familyId?: number
  createdBy?: number
}

export const transactionsApi = {
  list(params?: { startDate?: string; endDate?: string }) {
    return client.get('/transactions', { params })
  },
  get(id: number) {
    return client.get(`/transactions/${id}`)
  },
  create(data: Transaction) {
    return client.post('/transactions', data)
  },
  update(id: number, data: Transaction) {
    return client.put(`/transactions/${id}`, data)
  },
  delete(id: number) {
    return client.delete(`/transactions/${id}`)
  }
}
