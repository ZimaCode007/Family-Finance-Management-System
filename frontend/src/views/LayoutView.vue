<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #304156">
      <div class="logo">Family Finance</div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/assets">
          <el-icon><Wallet /></el-icon>
          <span>Assets</span>
        </el-menu-item>
        <el-menu-item index="/transactions">
          <el-icon><List /></el-icon>
          <span>Transactions</span>
        </el-menu-item>
        <el-menu-item index="/scores">
          <el-icon><Trophy /></el-icon>
          <span>Scores</span>
        </el-menu-item>
        <el-menu-item index="/recurring">
          <el-icon><Timer /></el-icon>
          <span>Recurring</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>Settings</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display: flex; align-items: center; justify-content: flex-end; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.1);">
        <span style="margin-right: 16px;">{{ authStore.nickname || authStore.username }}</span>
        <el-tag v-if="authStore.familyName" type="info" style="margin-right: 16px;">{{ authStore.familyName }}</el-tag>
        <el-button type="danger" size="small" @click="handleLogout">Logout</el-button>
      </el-header>

      <el-main style="background: #f0f2f5;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { DataBoard, Wallet, List, Trophy, Timer, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #263445;
}
</style>
