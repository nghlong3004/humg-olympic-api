package vn.edu.humg.olympic.api.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;

public record AssignmentUpdateRequest(
    @NotNull Long id,
    @NotNull @Size(min = 2, max = 25) String title,
    String description,
    String subjectName,
    Timestamp startTime,
    Timestamp endTime,
    Boolean isActive) {}
