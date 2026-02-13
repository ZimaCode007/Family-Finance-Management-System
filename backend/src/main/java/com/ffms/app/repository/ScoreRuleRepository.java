package com.ffms.app.repository;

import com.ffms.app.model.ScoreRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRuleRepository extends JpaRepository<ScoreRule, Long> {

    List<ScoreRule> findByFamilyId(Long familyId);
}
