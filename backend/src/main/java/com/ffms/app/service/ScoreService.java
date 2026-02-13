package com.ffms.app.service;

import com.ffms.app.model.ScoreRecord;
import com.ffms.app.model.ScoreRule;
import com.ffms.app.model.User;
import com.ffms.app.repository.ScoreRecordRepository;
import com.ffms.app.repository.ScoreRuleRepository;
import com.ffms.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRuleRepository scoreRuleRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final UserRepository userRepository;

    public List<ScoreRule> getRules(Long familyId) {
        return scoreRuleRepository.findByFamilyId(familyId);
    }

    public ScoreRule createRule(ScoreRule rule) {
        return scoreRuleRepository.save(rule);
    }

    public ScoreRule updateRule(Long id, ScoreRule updated) {
        ScoreRule rule = scoreRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Score rule not found"));
        rule.setName(updated.getName());
        rule.setDescription(updated.getDescription());
        rule.setPoints(updated.getPoints());
        return scoreRuleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        scoreRuleRepository.deleteById(id);
    }

    @Transactional
    public ScoreRecord recordScore(ScoreRecord record) {
        ScoreRule rule = scoreRuleRepository.findById(record.getScoreRuleId())
                .orElseThrow(() -> new RuntimeException("Score rule not found"));
        record.setPoints(rule.getPoints());
        return scoreRecordRepository.save(record);
    }

    public List<ScoreRecord> getRecords(Long familyId) {
        return scoreRecordRepository.findByFamilyIdOrderByCreatedAtDesc(familyId);
    }

    public List<Map<String, Object>> getLeaderboard(Long familyId) {
        List<Object[]> rows = scoreRecordRepository.getLeaderboard(familyId);
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        for (Object[] row : rows) {
            Long userId = (Long) row[0];
            Long totalPoints = (Long) row[1];
            User user = userRepository.findById(userId).orElse(null);

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("userId", userId);
            entry.put("username", user != null ? user.getUsername() : "Unknown");
            entry.put("nickname", user != null ? user.getNickname() : "Unknown");
            entry.put("totalPoints", totalPoints);
            leaderboard.add(entry);
        }

        return leaderboard;
    }
}
