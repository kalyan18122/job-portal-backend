package com.jobportal.controller;

import com.jobportal.dto.ProfileUpdateRequest;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Protected endpoint - requires a valid JWT (see SecurityConfig).
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = findCurrentUser(authentication);
        return ResponseEntity.ok(toProfileMap(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody ProfileUpdateRequest request,
                                                Authentication authentication) {
        User user = findCurrentUser(authentication);

        user.setFullName(request.getFullName());
        if (user.getRole() == com.jobportal.model.Role.RECRUITER) {
            user.setCompanyName(request.getCompanyName());
        } else {
            user.setSkills(request.getSkills());
            user.setResumeUrl(request.getResumeUrl());
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(toProfileMap(saved));
    }

    private User findCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, Object> toProfileMap(User user) {
        return Map.of(
                "id", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "companyName", user.getCompanyName() == null ? "" : user.getCompanyName(),
                "skills", user.getSkills() == null ? "" : user.getSkills(),
                "resumeUrl", user.getResumeUrl() == null ? "" : user.getResumeUrl()
        );
    }
}

