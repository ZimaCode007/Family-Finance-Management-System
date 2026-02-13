<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
      <h2 style="margin: 0;">Assets</h2>
      <el-button type="primary" @click="openDialog()">Add Asset</el-button>
    </div>

    <el-table :data="assets" stripe v-loading="loading">
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="type" label="Type" width="130">
        <template #default="{ row }">
          <el-tag>{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="Amount" width="150">
        <template #default="{ row }">
          ${{ Number(row.amount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="Description" />
      <el-table-column label="Actions" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? 'Edit Asset' : 'Add Asset'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="Name" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Type" required>
          <el-select v-model="form.type" style="width: 100%">
            <el-option v-for="t in assetTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="Amount" required>
          <el-input-number v-model="form.amount" :precision="2" :step="100" style="width: 100%" />
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
import { assetsApi, type Asset } from '@/api/assets'
import { ElMessage, ElMessageBox } from 'element-plus'

const assets = ref<Asset[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const assetTypes = ['CASH', 'BANK', 'INVESTMENT', 'PROPERTY', 'OTHER']

const form = reactive({
  name: '',
  type: 'CASH',
  amount: 0,
  description: ''
})

async function loadAssets() {
  loading.value = true
  try {
    const res = await assetsApi.list()
    assets.value = res.data.data
  } catch { ElMessage.error('Failed to load assets') }
  finally { loading.value = false }
}

function openDialog(asset?: Asset) {
  if (asset) {
    editingId.value = asset.id!
    form.name = asset.name
    form.type = asset.type
    form.amount = asset.amount
    form.description = asset.description || ''
  } else {
    editingId.value = null
    form.name = ''
    form.type = 'CASH'
    form.amount = 0
    form.description = ''
  }
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await assetsApi.update(editingId.value, { ...form })
      ElMessage.success('Asset updated')
    } else {
      await assetsApi.create({ ...form })
      ElMessage.success('Asset created')
    }
    dialogVisible.value = false
    await loadAssets()
  } catch { ElMessage.error('Failed to save asset') }
  finally { saving.value = false }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('Delete this asset?', 'Confirm')
  try {
    await assetsApi.delete(id)
    ElMessage.success('Asset deleted')
    await loadAssets()
  } catch { ElMessage.error('Failed to delete asset') }
}

onMounted(loadAssets)
</script>
