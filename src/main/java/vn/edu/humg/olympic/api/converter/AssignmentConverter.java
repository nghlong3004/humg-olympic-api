package vn.edu.humg.olympic.api.converter;

import java.util.List;
import java.util.stream.Collectors;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;

public class AssignmentConverter {
  public static Assignment from(AssignmentRequest request) {
    return Assignment.builder()
        .title(request.title())
        .description(request.description())
        .subjectName(request.subjectName())
        .startTime(request.startTime())
        .endTime(request.endTime())
        .build();
  }

  public static AssignmentResponse to(Assignment assignment) {
    return AssignmentResponse.builder()
        .id(assignment.getId())
        .title(assignment.getTitle())
        .description(assignment.getDescription())
        .subjectName(assignment.getSubjectName())
        .ownerId(assignment.getOwnerId())
        .startTime(assignment.getStartTime())
        .endTime(assignment.getEndTime())
        .isActive(assignment.getIsActive())
        .created(assignment.getCreated())
        .updated(assignment.getUpdated())
        .build();
  }

  public static List<AssignmentResponse> to(List<Assignment> assignments) {
    return assignments.stream().map(AssignmentConverter::to).collect(Collectors.toList());
  }

  private AssignmentConverter() {
    throw new UnsupportedOperationException("This class should never be instantiated");
  }
}
