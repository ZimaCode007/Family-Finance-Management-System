package com.ffms.app.service;

import com.ffms.app.model.Family;
import com.ffms.app.model.User;
import com.ffms.app.model.dto.AuthResponse;
import com.ffms.app.model.dto.LoginRequest;
import com.ffms.app.model.dto.RegisterRequest;
import com.ffms.app.model.enums.Role;
import com.ffms.app.repository.FamilyRepository;
import com.ffms.app.repository.UserRepository;
import com.ffms.app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        Family family = null;
        Role role = Role.MEMBER;

        if (request.familyName() != null && !request.familyName().isBlank()) {
            family = familyRepository.save(Family.builder()
                    .name(request.familyName())
                    .build());
            role = Role.ADMIN;
        } else if (request.familyId() != null) {
            family = familyRepository.findById(request.familyId())
                    .orElseThrow(() -> new RuntimeException("Family not found"));
        }

        User user = userRepository.save(User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname() != null ? request.nickname() : request.username())
                .role(role)
                .family(family)
                .build());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(),
                user.getRole().name(), family != null ? family.getId() : null);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getNickname(),
                user.getRole().name(), family != null ? family.getId() : null,
                family != null ? family.getName() : null);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Family family = user.getFamily();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(),
                user.getRole().name(), family != null ? family.getId() : null);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getNickname(),
                user.getRole().name(), family != null ? family.getId() : null,
                family != null ? family.getName() : null);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
