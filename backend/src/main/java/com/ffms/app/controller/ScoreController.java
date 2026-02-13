package com.ffms.app.controller;

import com.ffms.app.model.ScoreRecord;
import com.ffms.app.model.ScoreRule;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.security.JwtUtil;
import com.ffms.app.service.ScoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private final JwtUtil jwtUtil;

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<ScoreRule>>> getRules(HttpServletRequest request) {
        Long familyId = extractFamilyId(request);
        return ResponseEntity.ok(ApiResponse.ok(scoreService.getRules(familyId)));
    }

    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<ScoreRule>> createRule(@RequestBody ScoreRule rule, HttpServletRequest request) {
        rule.setFamilyId(extractFamilyId(request));
        return ResponseEntity.ok(ApiResponse.ok(scoreService.createRule(rule)));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<ScoreRule>> updateRule(@PathVariable Long id, @RequestBody ScoreRule rule) {
        return ResponseEntity.ok(ApiResponse.ok(scoreService.updateRule(id, rule)));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        scoreService.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.ok("Rule deleted", null));
    }

    @PostMapping("/records")
    public ResponseEntity<ApiResponse<ScoreRecord>> recordScore(@RequestBody ScoreRecord record, HttpServletRequest request) {
        record.setFamilyId(extractFamilyId(request));
        return ResponseEntity.ok(ApiResponse.ok(scoreService.recordScore(record)));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<ScoreRecord>>> getRecords(HttpServletRequest request) {
        Long familyId = extractFamilyId(request);
        return ResponseEntity.ok(ApiResponse.ok(scoreService.getRecords(familyId)));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLeaderboard(HttpServletRequest request) {
        Long familyId = extractFamilyId(request);
        return ResponseEntity.ok(ApiResponse.ok(scoreService.getLeaderboard(familyId)));
    }

    private Long extractFamilyId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractFamilyId(token);
    }
}
