import client from './client'

export interface ScoreRule {
  id?: number
  name: string
  description?: string
  points: number
  familyId?: number
}

export interface ScoreRecord {
  id?: number
  scoreRuleId: number
  userId: number
  familyId?: number
  points?: number
  note?: string
}

export interface LeaderboardEntry {
  userId: number
  username: string
  nickname: string
  totalPoints: number
}

export const scoresApi = {
  getRules() {
    return client.get('/scores/rules')
  },
  createRule(data: ScoreRule) {
    return client.post('/scores/rules', data)
  },
  updateRule(id: number, data: ScoreRule) {
    return client.put(`/scores/rules/${id}`, data)
  },
  deleteRule(id: number) {
    return client.delete(`/scores/rules/${id}`)
  },
  recordScore(data: ScoreRecord) {
    return client.post('/scores/records', data)
  },
  getRecords() {
    return client.get('/scores/records')
  },
  getLeaderboard() {
    return client.get('/scores/leaderboard')
  }
}
