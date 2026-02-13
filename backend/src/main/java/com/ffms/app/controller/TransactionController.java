package com.ffms.app.controller;

import com.ffms.app.model.Transaction;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.security.JwtUtil;
import com.ffms.app.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactions(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long familyId = extractFamilyId(request);

        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionService.getByFamilyAndDateRange(familyId, startDate, endDate);
        } else {
            transactions = transactionService.getByFamily(familyId);
        }
        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@RequestBody Transaction transaction, HttpServletRequest request) {
        transaction.setFamilyId(extractFamilyId(request));
        transaction.setCreatedBy(extractUserId(request));
        return ResponseEntity.ok(ApiResponse.ok(transactionService.create(transaction)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.update(id, transaction)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Transaction deleted", null));
    }

    private Long extractFamilyId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractFamilyId(token);
    }

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
