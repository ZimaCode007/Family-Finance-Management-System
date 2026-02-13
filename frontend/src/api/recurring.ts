import client from './client'

export interface RecurringEvent {
  id?: number
  name: string
  type: string
  amount: number
  category?: string
  description?: string
  frequency: string
  nextExecutionDate: string
  assetId?: number
  active?: boolean
  familyId?: number
  createdBy?: number
}

export const recurringApi = {
  list() {
    return client.get('/recurring-events')
  },
  get(id: number) {
    return client.get(`/recurring-events/${id}`)
  },
  create(data: RecurringEvent) {
    return client.post('/recurring-events', data)
  },
  update(id: number, data: RecurringEvent) {
    return client.put(`/recurring-events/${id}`, data)
  },
  toggleActive(id: number) {
    return client.patch(`/recurring-events/${id}/toggle`)
  },
  delete(id: number) {
    return client.delete(`/recurring-events/${id}`)
  }
}
