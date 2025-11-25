package vn.edu.humg.olympic.api.service.impl;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.humg.olympic.api.converter.AssignmentConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.request.AssignmentUpdateRequest;
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
    log.debug("Validation role:{}", currentUser.getAuthority());
    if (currentUser.isStudent()) {
      log.debug(
          "User with role {} is not allowed to create assignment", currentUser.getAuthority());
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }

    validateTimeRange(request.startTime(), request.endTime());

    var assignment = AssignmentConverter.from(request);
    assignment.setOwnerId(currentUser.getId());
    assignment.setIsActive(true);
    log.debug("Save assignment:{} by ownerId:{}", request, currentUser.getId());
    assignmentRepository.save(assignment);
    log.debug("Successfully save assignment");
  }

  @Override
  @Transactional(readOnly = true)
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

    log.debug("PageResponse (list) returned");
    return buildPageResponse(items, page, size, totalItems);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<AssignmentResponse> searchByTitle(int page, int size, String keyword) {
    log.debug("Search assignments by title → page={}, size={}, keyword='{}'", page, size, keyword);

    validatePageAndSize(page, size);

    if (keyword == null || keyword.isBlank()) {
      log.warn("Empty keyword in searchByTitle");
      throw new ResourceException(ErrorCode.INVALID_REQUEST);
    }

    int offset = page * size;
    String pattern = "%" + keyword.trim() + "%";
    var assignments = assignmentRepository.searchByTitlePaging(offset, size, pattern);
    long totalItems = assignmentRepository.countByTitle(pattern);
    int totalPages = (int) Math.ceil((double) totalItems / size);

    log.info(
        "Found {} assignments by title (page={}/{}, totalItems={}) [keyword='{}']",
        assignments.size(),
        page,
        totalPages,
        totalItems,
        keyword);

    var items = AssignmentConverter.to(assignments);

    log.debug("PageResponse (searchByTitle) returned");
    return buildPageResponse(items, page, size, totalItems);
  }

  @Override
  @Transactional
  public void update(AssignmentUpdateRequest request) {
    log.debug(
        "update assignment id:{} -> title:{}, description:{}, subjectName:{}, startTime:{}, endTime:{}",
        request.id(),
        request.title(),
        request.description(),
        request.subjectName(),
        request.startTime(),
        request.endTime());

    var assignment = findAssignmentOrThrow(request.id());
    var currentUser = userService.getCurrentUser();

    validateOwnershipOrAdmin(assignment, currentUser);
    validateAssignmentIsActive(assignment);
    validateTimeRange(request.startTime(), request.endTime());

    applyUpdate(assignment, request);

    assignmentRepository.update(assignment);
  }

  private Assignment findAssignmentOrThrow(Long id) {
    return assignmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceException(ErrorCode.NOT_FOUND));
  }

  private void validateOwnershipOrAdmin(Assignment assignment, AuthenticatedUser currentUser) {
    log.debug(
        "Check authority and owner assignment. authority:{}, currentUserId:{}, ownerId:{} ",
        currentUser.getAuthority(),
        currentUser.getId(),
        assignment.getOwnerId());

    if (!currentUser.isOwner(assignment.getOwnerId()) && !currentUser.isAdmin()) {
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }
  }

  private void validateAssignmentIsActive(Assignment assignment) {
    if (Boolean.FALSE.equals(assignment.getIsActive())) {
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }
  }

  private void validateTimeRange(Timestamp startTime, Timestamp endTime) {
    log.debug("check startTime:{} and endTime:{}", startTime, endTime);
    if (startTime != null && endTime != null && endTime.before(startTime)) {
      throw new ResourceException(ErrorCode.INVALID_REQUEST);
    }
  }

  private void applyUpdate(Assignment assignment, AssignmentUpdateRequest request) {
    if (request.title() != null) {
      assignment.setTitle(request.title());
    }
    if (request.description() != null) {
      assignment.setDescription(request.description());
    }
    if (request.subjectName() != null) {
      assignment.setSubjectName(request.subjectName());
    }
    if (request.startTime() != null) {
      assignment.setStartTime(request.startTime());
    }
    if (request.endTime() != null) {
      assignment.setEndTime(request.endTime());
    }
    if (request.isActive() != null) {
      assignment.setIsActive(request.isActive());
    }
  }

  private <T> PageResponse<T> buildPageResponse(
      List<T> items, int page, int size, long totalItems) {

    int totalPages = (int) Math.ceil((double) totalItems / size);

    return PageResponse.<T>builder()
        .items(items)
        .page(page)
        .size(size)
        .totalItems(totalItems)
        .totalPages(totalPages)
        .build();
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
