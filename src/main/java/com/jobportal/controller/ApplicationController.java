package com.jobportal.controller;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.StatusUpdateRequest;
import com.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Job seeker: apply to a job
    @PostMapping("/api/jobseeker/jobs/{jobId}/apply")
    public ResponseEntity<?> apply(@PathVariable Long jobId, Authentication authentication) {
        try {
            ApplicationResponse response = applicationService.apply(authentication.getName(), jobId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    // Job seeker: view my applications
    @GetMapping("/api/jobseeker/applications")
    public ResponseEntity<List<ApplicationResponse>> myApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getMyApplications(authentication.getName()));
    }

    // Recruiter: view applicants for a specific job posting
    @GetMapping("/api/recruiter/jobs/{jobId}/applicants")
    public ResponseEntity<?> applicantsForJob(@PathVariable Long jobId, Authentication authentication) {
        try {
            return ResponseEntity.ok(applicationService.getApplicantsForJob(authentication.getName(), jobId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    // Recruiter: update an applicant's status (SHORTLISTED, REJECTED, HIRED, etc.)
    @PutMapping("/api/recruiter/applications/{applicationId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long applicationId,
                                           @Valid @RequestBody StatusUpdateRequest request,
                                           Authentication authentication) {
        try {
            return ResponseEntity.ok(applicationService.updateStatus(authentication.getName(), applicationId, request.getStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }
}
