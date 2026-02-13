<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h2 style="margin: 0;">Recurring Events</h2>
      <el-button type="primary" @click="openDialog()">Add Event</el-button>
    </div>

    <el-table :data="events" stripe v-loading="loading">
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="type" label="Type" width="110">
        <template #default="{ row }">
          <el-tag :type="row.type === 'INCOME' ? 'success' : row.type === 'EXPENSE' ? 'danger' : 'warning'" size="small">
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="Amount" width="120">
        <template #default="{ row }">${{ Number(row.amount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="frequency" label="Frequency" width="110" />
      <el-table-column prop="nextExecutionDate" label="Next Date" width="130" />
      <el-table-column prop="active" label="Active" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.active" @change="handleToggle(row.id)" />
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? 'Edit Event' : 'Add Event'" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Name" required>
          <el-input v-model="form.name" />
        </el-form-item>
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
        <el-form-item label="Frequency" required>
          <el-select v-model="form.frequency" style="width: 100%">
            <el-option label="DAILY" value="DAILY" />
            <el-option label="WEEKLY" value="WEEKLY" />
            <el-option label="MONTHLY" value="MONTHLY" />
            <el-option label="YEARLY" value="YEARLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="Next Date" required>
          <el-date-picker v-model="form.nextExecutionDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
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
import { recurringApi, type RecurringEvent } from '@/api/recurring'
import { ElMessage, ElMessageBox } from 'element-plus'

const events = ref<RecurringEvent[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  type: 'EXPENSE',
  amount: 0,
  category: '',
  frequency: 'MONTHLY',
  nextExecutionDate: '',
  description: ''
})

async function loadEvents() {
  loading.value = true
  try {
    const res = await recurringApi.list()
    events.value = res.data.data
  } catch { ElMessage.error('Failed to load events') }
  finally { loading.value = false }
}

function openDialog(event?: RecurringEvent) {
  if (event) {
    editingId.value = event.id!
    form.name = event.name
    form.type = event.type
    form.amount = event.amount
    form.category = event.category || ''
    form.frequency = event.frequency
    form.nextExecutionDate = event.nextExecutionDate
    form.description = event.description || ''
  } else {
    editingId.value = null
    form.name = ''
    form.type = 'EXPENSE'
    form.amount = 0
    form.category = ''
    form.frequency = 'MONTHLY'
    form.nextExecutionDate = ''
    form.description = ''
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await recurringApi.update(editingId.value, { ...form })
      ElMessage.success('Event updated')
    } else {
      await recurringApi.create({ ...form, active: true })
      ElMessage.success('Event created')
    }
    dialogVisible.value = false
    await loadEvents()
  } catch { ElMessage.error('Failed to save event') }
  finally { saving.value = false }
}

async function handleToggle(id: number) {
  try {
    await recurringApi.toggleActive(id)
    await loadEvents()
  } catch { ElMessage.error('Failed to toggle event') }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Delete this event?', 'Confirm')
  try {
    await recurringApi.delete(id)
    ElMessage.success('Event deleted')
    await loadEvents()
  } catch { ElMessage.error('Failed to delete event') }
}

onMounted(loadEvents)
</script>
