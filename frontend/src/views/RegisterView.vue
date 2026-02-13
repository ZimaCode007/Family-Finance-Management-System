<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>Family Finance - Register</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="Username" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="Password" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="Nickname (optional)" size="large" />
        </el-form-item>
        <el-divider>Family</el-divider>
        <el-radio-group v-model="familyOption" style="margin-bottom: 16px;">
          <el-radio value="create">Create new family</el-radio>
          <el-radio value="join">Join existing family</el-radio>
          <el-radio value="skip">Skip for now</el-radio>
        </el-radio-group>
        <el-form-item v-if="familyOption === 'create'">
          <el-input v-model="form.familyName" placeholder="Family Name" size="large" />
        </el-form-item>
        <el-form-item v-if="familyOption === 'join'">
          <el-input v-model.number="form.familyId" placeholder="Family ID" size="large" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleRegister">
            Register
          </el-button>
        </el-form-item>
        <el-form-item>
          <span>Already have an account? <router-link to="/login">Login</router-link></span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage, type FormInstance } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const familyOption = ref('create')

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  familyName: '',
  familyId: undefined as number | undefined
})

const rules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 50, message: '3-50 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, message: 'At least 6 characters', trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const request: any = {
      username: form.username,
      password: form.password,
      nickname: form.nickname || undefined
    }
    if (familyOption.value === 'create' && form.familyName) {
      request.familyName = form.familyName
    } else if (familyOption.value === 'join' && form.familyId) {
      request.familyId = form.familyId
    }

    await authStore.register(request)
    ElMessage.success('Registration successful')
    router.push('/dashboard')
  } catch (err: any) {
    ElMessage.error(err.response?.data?.message || 'Registration failed')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}
.register-card {
  width: 450px;
}
.register-card h2 {
  margin: 0;
  text-align: center;
}
</style>
