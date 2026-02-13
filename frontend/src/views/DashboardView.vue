<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="Total Assets" :value="dashboard.totalAssets" prefix="$" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="Monthly Income" :value="dashboard.monthlyIncome" prefix="$" :value-style="{ color: '#67c23a' }" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="Monthly Expense" :value="dashboard.monthlyExpense" prefix="$" :value-style="{ color: '#f56c6c' }" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>Recent Transactions</template>
          <el-table :data="dashboard.recentTransactions" stripe size="small">
            <el-table-column prop="transactionDate" label="Date" width="120" />
            <el-table-column prop="type" label="Type" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 'INCOME' ? 'success' : row.type === 'EXPENSE' ? 'danger' : 'warning'" size="small">
                  {{ row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="Category" width="120" />
            <el-table-column prop="amount" label="Amount" width="120">
              <template #default="{ row }">
                ${{ Number(row.amount).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="Description" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>Expense by Category</template>
          <div v-if="Object.keys(dashboard.expenseByCategory).length === 0" style="text-align: center; color: #999; padding: 20px;">
            No expense data this month
          </div>
          <div v-else>
            <div v-for="(amount, category) in dashboard.expenseByCategory" :key="category" style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee;">
              <span>{{ category }}</span>
              <span style="font-weight: bold; color: #f56c6c;">${{ Number(amount).toFixed(2) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { dashboardApi } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const dashboard = reactive({
  totalAssets: 0,
  monthlyIncome: 0,
  monthlyExpense: 0,
  recentTransactions: [] as any[],
  expenseByCategory: {} as Record<string, number>
})

onMounted(async () => {
  try {
    const res = await dashboardApi.get()
    Object.assign(dashboard, res.data.data)
  } catch (err: any) {
    ElMessage.error('Failed to load dashboard')
  }
})
</script>
