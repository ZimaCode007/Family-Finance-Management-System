<template>
  <div>
    <h2>Family Settings</h2>

    <el-card style="margin-bottom: 20px;">
      <template #header>Family Info</template>
      <el-form :model="familyForm" label-width="120px" v-if="family">
        <el-form-item label="Family ID">
          <el-input :model-value="String(family.id)" disabled />
        </el-form-item>
        <el-form-item label="Name">
          <el-input v-model="familyForm.name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="familyForm.description" type="textarea" />
        </el-form-item>
        <el-form-item v-if="isAdmin">
          <el-button type="primary" @click="handleUpdateFamily">Save Changes</el-button>
        </el-form-item>
      </el-form>
      <el-empty v-else description="No family associated. Join or create one from registration." />
    </el-card>

    <el-card v-if="family">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>Members</span>
          <el-button v-if="isAdmin" type="primary" size="small" @click="addDialogVisible = true">Add Member</el-button>
        </div>
      </template>
      <el-table :data="members" stripe>
        <el-table-column prop="username" label="Username" />
        <el-table-column prop="nickname" label="Nickname" />
        <el-table-column prop="role" label="Role" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="120" v-if="isAdmin">
          <template #default="{ row }">
            <el-button v-if="row.role !== 'ADMIN'" size="small" type="danger" @click="handleRemove(row.id)">Remove</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="Add Member" width="400px">
      <el-form label-width="100px">
        <el-form-item label="Username">
          <el-input v-model="newUsername" placeholder="Enter username to add" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleAddMember">Add</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useFamilyStore } from '@/store/family'
import { ElMessage, ElMessageBox } from 'element-plus'

const authStore = useAuthStore()
const familyStore = useFamilyStore()

const family = computed(() => familyStore.family)
const members = computed(() => familyStore.members)
const isAdmin = computed(() => authStore.role === 'ADMIN')

const familyForm = reactive({ name: '', description: '' })
const addDialogVisible = ref(false)
const newUsername = ref('')

async function loadData() {
  if (!authStore.familyId) return
  try {
    await familyStore.loadFamily(authStore.familyId)
    await familyStore.loadMembers(authStore.familyId)
    if (familyStore.family) {
      familyForm.name = familyStore.family.name
      familyForm.description = familyStore.family.description || ''
    }
  } catch { ElMessage.error('Failed to load family data') }
}

async function handleUpdateFamily() {
  try {
    await familyStore.updateFamily(authStore.familyId!, familyForm)
    ElMessage.success('Family updated')
  } catch { ElMessage.error('Failed to update family') }
}

async function handleAddMember() {
  if (!newUsername.value) return
  try {
    await familyStore.addMember(authStore.familyId!, newUsername.value)
    addDialogVisible.value = false
    newUsername.value = ''
    ElMessage.success('Member added')
  } catch (err: any) {
    ElMessage.error(err.response?.data?.message || 'Failed to add member')
  }
}

async function handleRemove(userId: number) {
  await ElMessageBox.confirm('Remove this member from the family?', 'Confirm')
  try {
    await familyStore.removeMember(authStore.familyId!, userId)
    ElMessage.success('Member removed')
  } catch { ElMessage.error('Failed to remove member') }
}

onMounted(loadData)
</script>
