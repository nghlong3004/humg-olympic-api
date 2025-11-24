package vn.edu.humg.olympic.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.humg.olympic.api.converter.AssignmentConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.repository.AssignmentRepository;
import vn.edu.humg.olympic.api.service.AssignmentService;
import vn.edu.humg.olympic.api.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

  private final AssignmentRepository assignmentRepository;

  private final UserService userService;

  @Override
  public void create(AssignmentRequest request) {

    var currentUser = userService.getCurrentUser();
    log.debug("Validation role:{}", currentUser.getRole());
    if (Role.STUDENT.getAuthority().equals(currentUser.getRole())) {
      log.debug("Validation role=false");
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }

    var assignment = AssignmentConverter.from(request);
    assignment.setOwnerId(currentUser.getId());
    log.debug("Save assignment:{} by ownerId:{}", request, currentUser.getId());
    assignmentRepository.save(assignment);
    log.debug("Successfully save assignment");
  }

  @Override
  public PageResponse<AssignmentResponse> list(int page, int size) {
    log.debug("List assignments → page={}, size={}", page, size);

    validatePageAndSize(page, size);

    int offset = page * size;

    var assignments = assignmentRepository.findAllPaging(offset, size);
    long totalItems = assignmentRepository.countAll();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    log.info(
        "Found {} assignments (page={}/{}, totalItems={})",
        assignments.size(),
        page,
        totalPages,
        totalItems);

    var items = AssignmentConverter.to(assignments);

    var response =
        PageResponse.<AssignmentResponse>builder()
            .items(items)
            .page(page)
            .size(size)
            .totalItems(totalItems)
            .totalPages(totalPages)
            .build();

    log.debug("PageResponse (list) returned: {}", response);

    return response;
  }

  @Override
  public PageResponse<AssignmentResponse> searchByTitle(int page, int size, String keyword) {
    log.debug("Search assignments by title → page={}, size={}, keyword='{}'", page, size, keyword);

    validatePageAndSize(page, size);

    if (keyword == null || keyword.isBlank()) {
      log.warn("Empty keyword in searchByTitle");
      throw new ResourceException(ErrorCode.INVALID_REQUEST);
    }

    int offset = page * size;

    var assignments = assignmentRepository.searchByTitlePaging(offset, size, keyword);
    long totalItems = assignmentRepository.countByTitle(keyword);
    int totalPages = (int) Math.ceil((double) totalItems / size);

    log.info(
        "Found {} assignments by title (page={}/{}, totalItems={}) [keyword='{}']",
        assignments.size(),
        page,
        totalPages,
        totalItems,
        keyword);

    var items = AssignmentConverter.to(assignments);

    var response =
        PageResponse.<AssignmentResponse>builder()
            .items(items)
            .page(page)
            .size(size)
            .totalItems(totalItems)
            .totalPages(totalPages)
            .build();

    log.debug("PageResponse (searchByTitle) returned: {}", response);

    return response;
  }

  private void validatePageAndSize(int page, int size) {
    if (page < 0) {
      log.warn("Invalid page: {}", page);
      throw new ResourceException(ErrorCode.INVALID_REQUEST);
    }
    if (size <= 0 || size > 20) {
      log.warn("Invalid size (must be 1..20): {}", size);
      throw new ResourceException(ErrorCode.INVALID_REQUEST);
    }
  }
}
