package com.ffms.app.controller;

import com.ffms.app.model.Transaction;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.model.dto.DashboardResponse;
import com.ffms.app.model.enums.TransactionType;
import com.ffms.app.repository.TransactionRepository;
import com.ffms.app.security.JwtUtil;
import com.ffms.app.service.AssetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AssetService assetService;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long familyId = jwtUtil.extractFamilyId(token);

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal totalAssets = assetService.getTotalByFamily(familyId);
        BigDecimal monthlyIncome = transactionRepository.sumByFamilyIdAndTypeAndDateBetween(
                familyId, TransactionType.INCOME, monthStart, monthEnd);
        BigDecimal monthlyExpense = transactionRepository.sumByFamilyIdAndTypeAndDateBetween(
                familyId, TransactionType.EXPENSE, monthStart, monthEnd);

        List<Transaction> recentTransactions = transactionRepository
                .findTop10ByFamilyIdOrderByTransactionDateDesc(familyId);

        List<Object[]> categoryRows = transactionRepository
                .sumExpenseByCategoryAndDateBetween(familyId, monthStart, monthEnd);

        Map<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();
        for (Object[] row : categoryRows) {
            String category = row[0] != null ? (String) row[0] : "Uncategorized";
            BigDecimal amount = (BigDecimal) row[1];
            expenseByCategory.put(category, amount);
        }

        DashboardResponse dashboard = new DashboardResponse(
                totalAssets, monthlyIncome, monthlyExpense, recentTransactions, expenseByCategory);

        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }
}
