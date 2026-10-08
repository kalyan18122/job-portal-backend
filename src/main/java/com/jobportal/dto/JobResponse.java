package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private String jobType;
    private String skillsRequired;
    private String salaryRange;
    private Long postedByUserId;
    private String postedByName;
    private LocalDateTime createdAt;
    private Integer applicantCount; // populated only on recruiter's own-postings view
    private Boolean alreadyApplied; // populated only on job seeker's search/detail view
}
