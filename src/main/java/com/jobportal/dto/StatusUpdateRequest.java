package com.jobportal.dto;

import com.jobportal.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
