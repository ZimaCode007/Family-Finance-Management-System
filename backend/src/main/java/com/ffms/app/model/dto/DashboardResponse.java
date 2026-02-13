package com.ffms.app.model.dto;

import com.ffms.app.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        BigDecimal totalAssets,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpense,
        List<Transaction> recentTransactions,
        Map<String, BigDecimal> expenseByCategory
) {}
