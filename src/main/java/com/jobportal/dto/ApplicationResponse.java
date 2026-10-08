package com.jobportal.dto;

import com.jobportal.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    // Job summary
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;

    // Applicant summary (populated for recruiter's applicant-list view)
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String applicantSkills;
    private String applicantResumeUrl;
}
