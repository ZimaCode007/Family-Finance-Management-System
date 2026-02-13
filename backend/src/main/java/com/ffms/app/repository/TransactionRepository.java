package com.ffms.app.repository;

import com.ffms.app.model.Transaction;
import com.ffms.app.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFamilyIdOrderByTransactionDateDesc(Long familyId);

    List<Transaction> findByFamilyIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long familyId, LocalDate start, LocalDate end);

    List<Transaction> findByFamilyIdAndTypeOrderByTransactionDateDesc(Long familyId, TransactionType type);

    List<Transaction> findTop10ByFamilyIdOrderByTransactionDateDesc(Long familyId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.familyId = :familyId AND t.type = :type AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByFamilyIdAndTypeAndDateBetween(Long familyId, TransactionType type, LocalDate start, LocalDate end);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.familyId = :familyId AND t.type = 'EXPENSE' AND t.transactionDate BETWEEN :start AND :end GROUP BY t.category")
    List<Object[]> sumExpenseByCategoryAndDateBetween(Long familyId, LocalDate start, LocalDate end);
}
