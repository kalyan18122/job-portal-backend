package com.jobportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt-hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Extra fields relevant to a job seeker; ignored for recruiters
    private String skills;
    private String resumeUrl;

    // Extra field relevant to a recruiter; ignored for job seekers
    private String companyName;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
