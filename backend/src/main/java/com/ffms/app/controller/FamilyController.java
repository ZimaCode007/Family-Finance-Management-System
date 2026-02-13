package com.ffms.app.controller;

import com.ffms.app.model.Family;
import com.ffms.app.model.User;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Family>> getFamily(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(familyService.getFamily(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Family>> updateFamily(@PathVariable Long id, @RequestBody Family family) {
        return ResponseEntity.ok(ApiResponse.ok(familyService.updateFamily(id, family)));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<User>>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(familyService.getMembers(id)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<Void>> addMember(@PathVariable Long id, @RequestBody Map<String, String> body) {
        familyService.addMember(id, body.get("username"));
        return ResponseEntity.ok(ApiResponse.ok("Member added", null));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        familyService.removeMember(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Member removed", null));
    }
}
