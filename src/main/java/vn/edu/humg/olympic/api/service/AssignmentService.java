package vn.edu.humg.olympic.api.service;

import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.request.AssignmentUpdateRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;
import vn.edu.humg.olympic.api.model.response.PageResponse;

public interface AssignmentService {
  void create(AssignmentRequest request);

  PageResponse<AssignmentResponse> list(int page, int size);

  PageResponse<AssignmentResponse> search(int page, int size, String keyword);

  void update(AssignmentUpdateRequest request);

  void delete(Long id);
}
