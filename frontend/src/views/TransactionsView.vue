<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h2 style="margin: 0;">Transactions</h2>
      <div>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="to" start-placeholder="Start" end-placeholder="End" style="margin-right: 12px;" value-format="YYYY-MM-DD" @change="loadTransactions" />
        <el-button type="primary" @click="openDialog()">Add Transaction</el-button>
      </div>
    </div>

    <el-table :data="transactions" stripe v-loading="loading">
      <el-table-column prop="transactionDate" label="Date" width="120" />
      <el-table-column prop="type" label="Type" width="110">
        <template #default="{ row }">
          <el-tag :type="row.type === 'INCOME' ? 'success' : row.type === 'EXPENSE' ? 'danger' : 'warning'" size="small">
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="Amount" width="130">
        <template #default="{ row }">${{ Number(row.amount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="category" label="Category" width="130" />
      <el-table-column prop="description" label="Description" />
      <el-table-column label="Actions" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? 'Edit Transaction' : 'Add Transaction'" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Type" required>
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="INCOME" value="INCOME" />
            <el-option label="EXPENSE" value="EXPENSE" />
            <el-option label="TRANSFER" value="TRANSFER" />
          </el-select>
        </el-form-item>
        <el-form-item label="Amount" required>
          <el-input-number v-model="form.amount" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="Date" required>
          <el-date-picker v-model="form.transactionDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { transactionsApi, type Transaction } from '@/api/transactions'
import { ElMessage, ElMessageBox } from 'element-plus'

const transactions = ref<Transaction[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const dateRange = ref<string[] | null>(null)

const form = reactive({
  type: 'EXPENSE',
  amount: 0,
  category: '',
  description: '',
  transactionDate: new Date().toISOString().slice(0, 10)
})

async function loadTransactions() {
  loading.value = true
  try {
    const params: any = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await transactionsApi.list(params)
    transactions.value = res.data.data
  } catch { ElMessage.error('Failed to load transactions') }
  finally { loading.value = false }
}

function openDialog(tx?: Transaction) {
  if (tx) {
    editingId.value = tx.id!
    form.type = tx.type
    form.amount = tx.amount
    form.category = tx.category || ''
    form.description = tx.description || ''
    form.transactionDate = tx.transactionDate
  } else {
    editingId.value = null
    form.type = 'EXPENSE'
    form.amount = 0
    form.category = ''
    form.description = ''
    form.transactionDate = new Date().toISOString().slice(0, 10)
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await transactionsApi.update(editingId.value, { ...form })
      ElMessage.success('Transaction updated')
    } else {
      await transactionsApi.create({ ...form })
      ElMessage.success('Transaction created')
    }
    dialogVisible.value = false
    await loadTransactions()
  } catch { ElMessage.error('Failed to save transaction') }
  finally { saving.value = false }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Delete this transaction?', 'Confirm')
  try {
    await transactionsApi.delete(id)
    ElMessage.success('Transaction deleted')
    await loadTransactions()
  } catch { ElMessage.error('Failed to delete transaction') }
}

onMounted(loadTransactions)
</script>
