package com.ffms.app.repository;

import com.ffms.app.model.ScoreRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, Long> {

    List<ScoreRecord> findByFamilyIdOrderByCreatedAtDesc(Long familyId);

    @Query("SELECT sr.userId, SUM(sr.points) FROM ScoreRecord sr WHERE sr.familyId = :familyId GROUP BY sr.userId ORDER BY SUM(sr.points) DESC")
    List<Object[]> getLeaderboard(Long familyId);
}
