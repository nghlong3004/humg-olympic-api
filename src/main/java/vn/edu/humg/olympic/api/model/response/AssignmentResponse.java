package vn.edu.humg.olympic.api.model.response;

import java.sql.Timestamp;
import lombok.Builder;

@Builder
public record AssignmentResponse(
    Long id,
    String title,
    String description,
    String subjectName,
    Long ownerId,
    Timestamp startTime,
    Timestamp endTime,
    Boolean isActive,
    Timestamp created,
    Timestamp updated) {}
