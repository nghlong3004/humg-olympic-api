package vn.edu.humg.olympic.api.converter;

import org.springframework.stereotype.Component;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;

@Component
public class AssignmentConverter {

  public Assignment from(AssignmentRequest request) {
    return Assignment.builder()
        .title(request.title())
        .description(request.description())
        .subjectName(request.subjectName())
        .startTime(request.startTime())
        .endTime(request.endTime())
        .build();
  }
}
