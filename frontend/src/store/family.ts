import { defineStore } from 'pinia'
import { ref } from 'vue'
import { familyApi, type Family, type FamilyMember } from '@/api/family'

export const useFamilyStore = defineStore('family', () => {
  const family = ref<Family | null>(null)
  const members = ref<FamilyMember[]>([])

  async function loadFamily(id: number) {
    const res = await familyApi.getFamily(id)
    family.value = res.data.data
  }

  async function loadMembers(id: number) {
    const res = await familyApi.getMembers(id)
    members.value = res.data.data
  }

  async function updateFamily(id: number, data: Partial<Family>) {
    const res = await familyApi.updateFamily(id, data)
    family.value = res.data.data
  }

  async function addMember(familyId: number, username: string) {
    await familyApi.addMember(familyId, username)
    await loadMembers(familyId)
  }

  async function removeMember(familyId: number, userId: number) {
    await familyApi.removeMember(familyId, userId)
    await loadMembers(familyId)
  }

  return { family, members, loadFamily, loadMembers, updateFamily, addMember, removeMember }
})
