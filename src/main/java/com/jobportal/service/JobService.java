package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(String recruiterEmail, JobRequest request) {
        User recruiter = getUser(recruiterEmail);

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .skillsRequired(request.getSkillsRequired())
                .salaryRange(request.getSalaryRange())
                .postedBy(recruiter)
                .build();

        Job saved = jobRepository.save(job);
        return toResponse(saved, null, null);
    }

    public JobResponse updateJob(String recruiterEmail, Long jobId, JobRequest request) {
        User recruiter = getUser(recruiterEmail);
        Job job = getOwnedJob(jobId, recruiter);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setSalaryRange(request.getSalaryRange());

        Job saved = jobRepository.save(job);
        return toResponse(saved, null, null);
    }

    public void deleteJob(String recruiterEmail, Long jobId) {
        User recruiter = getUser(recruiterEmail);
        Job job = getOwnedJob(jobId, recruiter);
        jobRepository.delete(job);
    }

    public List<JobResponse> getMyPostings(String recruiterEmail) {
        User recruiter = getUser(recruiterEmail);
        return jobRepository.findByPostedByOrderByCreatedAtDesc(recruiter).stream()
                .map(job -> {
                    int applicantCount = applicationRepository.findByJobOrderByAppliedAtDesc(job).size();
                    return toResponse(job, applicantCount, null);
                })
                .toList();
    }

    public List<JobResponse> search(String keyword, String location, String jobType, String viewerEmail) {
        User viewer = viewerEmail != null ? userRepository.findByEmail(viewerEmail).orElse(null) : null;

        return jobRepository.search(
                        emptyToNull(keyword), emptyToNull(location), emptyToNull(jobType))
                .stream()
                .map(job -> {
                    Boolean alreadyApplied = viewer != null
                            ? applicationRepository.existsByJobAndApplicant(job, viewer)
                            : null;
                    return toResponse(job, null, alreadyApplied);
                })
                .toList();
    }

    public JobResponse getJobDetail(Long jobId, String viewerEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        User viewer = viewerEmail != null ? userRepository.findByEmail(viewerEmail).orElse(null) : null;
        Boolean alreadyApplied = viewer != null
                ? applicationRepository.existsByJobAndApplicant(job, viewer)
                : null;
        return toResponse(job, null, alreadyApplied);
    }

    // --- helpers ---

    private Job getOwnedJob(Long jobId, User recruiter) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (!job.getPostedBy().getId().equals(recruiter.getId())) {
            throw new SecurityException("You do not own this job posting");
        }
        return job;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private JobResponse toResponse(Job job, Integer applicantCount, Boolean alreadyApplied) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .skillsRequired(job.getSkillsRequired())
                .salaryRange(job.getSalaryRange())
                .postedByUserId(job.getPostedBy().getId())
                .postedByName(job.getPostedBy().getFullName())
                .createdAt(job.getCreatedAt())
                .applicantCount(applicantCount)
                .alreadyApplied(alreadyApplied)
                .build();
    }
}
