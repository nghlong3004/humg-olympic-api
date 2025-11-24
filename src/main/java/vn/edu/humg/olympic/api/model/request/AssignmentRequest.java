package vn.edu.humg.olympic.api.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;
import lombok.Builder;

@Builder
public record AssignmentRequest(
    @NotNull @Size(min = 2, max = 25) String title,
    String description,
    String subjectName,
    Timestamp startTime,
    Timestamp endTime) {}
