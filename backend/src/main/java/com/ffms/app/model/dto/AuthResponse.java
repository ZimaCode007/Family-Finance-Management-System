package com.ffms.app.model.dto;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String nickname,
        String role,
        Long familyId,
        String familyName
) {}
