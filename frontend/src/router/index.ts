import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/',
      component: () => import('@/views/LayoutView.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/dashboard'
        },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'assets',
          name: 'Assets',
          component: () => import('@/views/AssetsView.vue')
        },
        {
          path: 'transactions',
          name: 'Transactions',
          component: () => import('@/views/TransactionsView.vue')
        },
        {
          path: 'scores',
          name: 'Scores',
          component: () => import('@/views/ScoresView.vue')
        },
        {
          path: 'recurring',
          name: 'Recurring',
          component: () => import('@/views/RecurringView.vue')
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('@/views/FamilySettingsView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn()) {
    next('/login')
  } else {
    next()
  }
})

export default router
