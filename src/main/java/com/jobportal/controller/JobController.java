package com.jobportal.controller;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.service.JobService;
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
public class JobController {

    private final JobService jobService;

    // Public search - visible to any authenticated user (job seeker or recruiter)
    @GetMapping("/api/jobs")
    public ResponseEntity<List<JobResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            Authentication authentication) {
        String viewerEmail = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(jobService.search(keyword, location, jobType, viewerEmail));
    }

    @GetMapping("/api/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id, Authentication authentication) {
        try {
            String viewerEmail = authentication != null ? authentication.getName() : null;
            return ResponseEntity.ok(jobService.getJobDetail(id, viewerEmail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // Recruiter-only: create, update, delete, and list own postings
    @PostMapping("/api/recruiter/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request, Authentication authentication) {
        JobResponse response = jobService.createJob(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/recruiter/jobs/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(jobService.updateJob(authentication.getName(), id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/api/recruiter/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication) {
        try {
            jobService.deleteJob(authentication.getName(), id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/api/recruiter/jobs")
    public ResponseEntity<List<JobResponse>> myPostings(Authentication authentication) {
        return ResponseEntity.ok(jobService.getMyPostings(authentication.getName()));
    }
}
