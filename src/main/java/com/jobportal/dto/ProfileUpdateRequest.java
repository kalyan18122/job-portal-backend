package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    // Job seeker fields (ignored for recruiters)
    private String skills;
    private String resumeUrl;

    // Recruiter field (ignored for job seekers)
    private String companyName;
}
