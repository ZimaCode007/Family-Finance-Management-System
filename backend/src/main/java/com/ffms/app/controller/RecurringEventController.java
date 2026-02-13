package com.ffms.app.controller;

import com.ffms.app.model.RecurringEvent;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.security.JwtUtil;
import com.ffms.app.service.RecurringEventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-events")
@RequiredArgsConstructor
public class RecurringEventController {

    private final RecurringEventService recurringEventService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringEvent>>> getEvents(HttpServletRequest request) {
        Long familyId = extractFamilyId(request);
        return ResponseEntity.ok(ApiResponse.ok(recurringEventService.getByFamily(familyId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringEvent>> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(recurringEventService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecurringEvent>> createEvent(@RequestBody RecurringEvent event, HttpServletRequest request) {
        event.setFamilyId(extractFamilyId(request));
        event.setCreatedBy(extractUserId(request));
        return ResponseEntity.ok(ApiResponse.ok(recurringEventService.create(event)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringEvent>> updateEvent(@PathVariable Long id, @RequestBody RecurringEvent event) {
        return ResponseEntity.ok(ApiResponse.ok(recurringEventService.update(id, event)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<RecurringEvent>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(recurringEventService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        recurringEventService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Event deleted", null));
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
