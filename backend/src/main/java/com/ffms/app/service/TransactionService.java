package com.ffms.app.service;

import com.ffms.app.model.Asset;
import com.ffms.app.model.Transaction;
import com.ffms.app.model.enums.TransactionType;
import com.ffms.app.repository.AssetRepository;
import com.ffms.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;

    public List<Transaction> getByFamily(Long familyId) {
        return transactionRepository.findByFamilyIdOrderByTransactionDateDesc(familyId);
    }

    public List<Transaction> getByFamilyAndDateRange(Long familyId, LocalDate start, LocalDate end) {
        return transactionRepository.findByFamilyIdAndTransactionDateBetweenOrderByTransactionDateDesc(familyId, start, end);
    }

    public Transaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);
        updateAssetAmount(saved);
        return saved;
    }

    @Transactional
    public Transaction update(Long id, Transaction updated) {
        Transaction transaction = getById(id);
        transaction.setType(updated.getType());
        transaction.setAmount(updated.getAmount());
        transaction.setCategory(updated.getCategory());
        transaction.setDescription(updated.getDescription());
        transaction.setTransactionDate(updated.getTransactionDate());
        transaction.setAssetId(updated.getAssetId());
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    private void updateAssetAmount(Transaction transaction) {
        if (transaction.getAssetId() == null) return;

        assetRepository.findById(transaction.getAssetId()).ifPresent(asset -> {
            if (transaction.getType() == TransactionType.INCOME) {
                asset.setAmount(asset.getAmount().add(transaction.getAmount()));
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                asset.setAmount(asset.getAmount().subtract(transaction.getAmount()));
            }
            assetRepository.save(asset);
        });
    }
}
