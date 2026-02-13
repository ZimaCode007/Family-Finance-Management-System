import client from './client'

export interface DashboardData {
  totalAssets: number
  monthlyIncome: number
  monthlyExpense: number
  recentTransactions: any[]
  expenseByCategory: Record<string, number>
}

export const dashboardApi = {
  get() {
    return client.get<{ success: boolean; data: DashboardData }>('/dashboard')
  }
}
