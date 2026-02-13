package com.ffms.app.repository;

import com.ffms.app.model.RecurringEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecurringEventRepository extends JpaRepository<RecurringEvent, Long> {

    List<RecurringEvent> findByFamilyIdOrderByNextExecutionDateAsc(Long familyId);

    List<RecurringEvent> findByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}
