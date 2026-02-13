package com.ffms.app.service;

import com.ffms.app.model.RecurringEvent;
import com.ffms.app.model.Transaction;
import com.ffms.app.model.enums.Frequency;
import com.ffms.app.repository.RecurringEventRepository;
import com.ffms.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringEventService {

    private final RecurringEventRepository recurringEventRepository;
    private final TransactionRepository transactionRepository;

    public List<RecurringEvent> getByFamily(Long familyId) {
        return recurringEventRepository.findByFamilyIdOrderByNextExecutionDateAsc(familyId);
    }

    public RecurringEvent getById(Long id) {
        return recurringEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring event not found"));
    }

    public RecurringEvent create(RecurringEvent event) {
        return recurringEventRepository.save(event);
    }

    public RecurringEvent update(Long id, RecurringEvent updated) {
        RecurringEvent event = getById(id);
        event.setName(updated.getName());
        event.setType(updated.getType());
        event.setAmount(updated.getAmount());
        event.setCategory(updated.getCategory());
        event.setDescription(updated.getDescription());
        event.setFrequency(updated.getFrequency());
        event.setNextExecutionDate(updated.getNextExecutionDate());
        event.setAssetId(updated.getAssetId());
        return recurringEventRepository.save(event);
    }

    public RecurringEvent toggleActive(Long id) {
        RecurringEvent event = getById(id);
        event.setActive(!event.getActive());
        return recurringEventRepository.save(event);
    }

    public void delete(Long id) {
        recurringEventRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void processRecurringEvents() {
        LocalDate today = LocalDate.now();
        List<RecurringEvent> dueEvents = recurringEventRepository
                .findByActiveTrueAndNextExecutionDateLessThanEqual(today);

        for (RecurringEvent event : dueEvents) {
            try {
                Transaction transaction = Transaction.builder()
                        .type(event.getType())
                        .amount(event.getAmount())
                        .category(event.getCategory())
                        .description("[Auto] " + event.getName())
                        .transactionDate(event.getNextExecutionDate())
                        .assetId(event.getAssetId())
                        .familyId(event.getFamilyId())
                        .createdBy(event.getCreatedBy())
                        .build();
                transactionRepository.save(transaction);

                event.setNextExecutionDate(calculateNextDate(event.getNextExecutionDate(), event.getFrequency()));
                recurringEventRepository.save(event);

                log.info("Processed recurring event: {} for family {}", event.getName(), event.getFamilyId());
            } catch (Exception e) {
                log.error("Failed to process recurring event {}: {}", event.getId(), e.getMessage());
            }
        }
    }

    private LocalDate calculateNextDate(LocalDate current, Frequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }
}
