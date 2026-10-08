package com.jobportal.service;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationResponse apply(String applicantEmail, Long jobId) {
        User applicant = getUser(applicantEmail);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (applicationRepository.existsByJobAndApplicant(job, applicant)) {
            throw new IllegalStateException("You have already applied to this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .status(ApplicationStatus.APPLIED)
                .build();

        JobApplication saved = applicationRepository.save(application);
        return toResponse(saved, true, false);
    }

    public List<ApplicationResponse> getMyApplications(String applicantEmail) {
        User applicant = getUser(applicantEmail);
        return applicationRepository.findByApplicantOrderByAppliedAtDesc(applicant).stream()
                .map(app -> toResponse(app, true, false))
                .toList();
    }

    public List<ApplicationResponse> getApplicantsForJob(String recruiterEmail, Long jobId) {
        User recruiter = getUser(recruiterEmail);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!job.getPostedBy().getId().equals(recruiter.getId())) {
            throw new SecurityException("You do not own this job posting");
        }

        return applicationRepository.findByJobOrderByAppliedAtDesc(job).stream()
                .map(app -> toResponse(app, false, true))
                .toList();
    }

    public ApplicationResponse updateStatus(String recruiterEmail, Long applicationId, ApplicationStatus status) {
        User recruiter = getUser(recruiterEmail);
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getJob().getPostedBy().getId().equals(recruiter.getId())) {
            throw new SecurityException("You do not own the job for this application");
        }

        application.setStatus(status);
        JobApplication saved = applicationRepository.save(application);
        return toResponse(saved, false, true);
    }

    // --- helpers ---

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ApplicationResponse toResponse(JobApplication app, boolean includeJob, boolean includeApplicant) {
        ApplicationResponse.ApplicationResponseBuilder builder = ApplicationResponse.builder()
                .applicationId(app.getId())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getCompanyName())
                .location(app.getJob().getLocation());

        if (includeApplicant) {
            User a = app.getApplicant();
            builder.applicantId(a.getId())
                    .applicantName(a.getFullName())
                    .applicantEmail(a.getEmail())
                    .applicantSkills(a.getSkills())
                    .applicantResumeUrl(a.getResumeUrl());
        }

        return builder.build();
    }
}
