package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Location is required")
    private String location;

    private String jobType;      // FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT, REMOTE
    private String skillsRequired;
    private String salaryRange;
}
