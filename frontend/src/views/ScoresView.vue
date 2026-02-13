<template>
  <div>
    <h2>Scores</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="Rules" name="rules">
        <div style="margin-bottom: 12px; text-align: right;">
          <el-button type="primary" @click="openRuleDialog()">Add Rule</el-button>
        </div>
        <el-table :data="rules" stripe>
          <el-table-column prop="name" label="Name" />
          <el-table-column prop="description" label="Description" />
          <el-table-column prop="points" label="Points" width="100" />
          <el-table-column label="Actions" width="250">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="openRecordDialog(row)">Record</el-button>
              <el-button size="small" @click="openRuleDialog(row)">Edit</el-button>
              <el-button size="small" type="danger" @click="handleDeleteRule(row.id)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="Leaderboard" name="leaderboard">
        <el-table :data="leaderboard" stripe>
          <el-table-column type="index" label="Rank" width="70" />
          <el-table-column prop="nickname" label="Name" />
          <el-table-column prop="totalPoints" label="Total Points" width="130">
            <template #default="{ row }">
              <span style="font-weight: bold; color: #e6a23c;">{{ row.totalPoints }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="History" name="history">
        <el-table :data="records" stripe>
          <el-table-column prop="createdAt" label="Date" width="180" />
          <el-table-column prop="userId" label="User ID" width="100" />
          <el-table-column prop="points" label="Points" width="100" />
          <el-table-column prop="note" label="Note" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Rule dialog -->
    <el-dialog v-model="ruleDialogVisible" :title="editingRuleId ? 'Edit Rule' : 'Add Rule'" width="450px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="Name" required>
          <el-input v-model="ruleForm.name" />
        </el-form-item>
        <el-form-item label="Points" required>
          <el-input-number v-model="ruleForm.points" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="ruleForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSaveRule">Save</el-button>
      </template>
    </el-dialog>

    <!-- Record score dialog -->
    <el-dialog v-model="recordDialogVisible" title="Record Score" width="400px">
      <el-form :model="recordForm" label-width="100px">
        <el-form-item label="Rule">
          <span>{{ selectedRuleName }} ({{ selectedRulePoints }} pts)</span>
        </el-form-item>
        <el-form-item label="User ID" required>
          <el-input-number v-model="recordForm.userId" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Note">
          <el-input v-model="recordForm.note" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleRecordScore">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { scoresApi, type ScoreRule } from '@/api/scores'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('rules')
const rules = ref<any[]>([])
const leaderboard = ref<any[]>([])
const records = ref<any[]>([])

const ruleDialogVisible = ref(false)
const recordDialogVisible = ref(false)
const editingRuleId = ref<number | null>(null)
const selectedRuleName = ref('')
const selectedRulePoints = ref(0)

const ruleForm = reactive({ name: '', description: '', points: 0 })
const recordForm = reactive({ scoreRuleId: 0, userId: 0, note: '' })

async function loadAll() {
  try {
    const [rulesRes, lbRes, recRes] = await Promise.all([
      scoresApi.getRules(), scoresApi.getLeaderboard(), scoresApi.getRecords()
    ])
    rules.value = rulesRes.data.data
    leaderboard.value = lbRes.data.data
    records.value = recRes.data.data
  } catch { ElMessage.error('Failed to load scores') }
}

function openRuleDialog(rule?: ScoreRule) {
  if (rule) {
    editingRuleId.value = rule.id!
    ruleForm.name = rule.name
    ruleForm.description = rule.description || ''
    ruleForm.points = rule.points
  } else {
    editingRuleId.value = null
    ruleForm.name = ''
    ruleForm.description = ''
    ruleForm.points = 0
  }
  ruleDialogVisible.value = true
}

function openRecordDialog(rule: ScoreRule) {
  recordForm.scoreRuleId = rule.id!
  recordForm.userId = 0
  recordForm.note = ''
  selectedRuleName.value = rule.name
  selectedRulePoints.value = rule.points
  recordDialogVisible.value = true
}

async function handleSaveRule() {
  try {
    if (editingRuleId.value) {
      await scoresApi.updateRule(editingRuleId.value, { ...ruleForm })
    } else {
      await scoresApi.createRule({ ...ruleForm })
    }
    ruleDialogVisible.value = false
    ElMessage.success('Rule saved')
    await loadAll()
  } catch { ElMessage.error('Failed to save rule') }
}

async function handleDeleteRule(id: number) {
  await ElMessageBox.confirm('Delete this rule?', 'Confirm')
  try {
    await scoresApi.deleteRule(id)
    ElMessage.success('Rule deleted')
    await loadAll()
  } catch { ElMessage.error('Failed to delete rule') }
}

async function handleRecordScore() {
  try {
    await scoresApi.recordScore({ ...recordForm })
    recordDialogVisible.value = false
    ElMessage.success('Score recorded')
    await loadAll()
  } catch { ElMessage.error('Failed to record score') }
}

onMounted(loadAll)
</script>
