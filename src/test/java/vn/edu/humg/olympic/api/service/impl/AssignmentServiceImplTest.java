package vn.edu.humg.olympic.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import vn.edu.humg.olympic.api.converter.AssignmentConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.request.AssignmentUpdateRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.repository.AssignmentRepository;
import vn.edu.humg.olympic.api.service.UserService;
import vn.edu.humg.olympic.api.util.GenerateRandom;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

  @Mock private AssignmentRepository assignmentRepository;

  @Mock private UserService userService;

  @InjectMocks private AssignmentServiceImpl assignmentService;

  @Test
  void create_shouldSaveAssignment_whenCurrentUserIsNotStudent() {
    int n = GenerateRandom.generateNumber(15);
    for (int i = 0; i < n; ++i) {
      var request = getGenerateAssignmentRequest();

      long userId = GenerateRandom.generateNumber(1_000_000);
      var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
      var currentUser =
          AuthenticatedUser.builder()
              .id(userId)
              .username(GenerateRandom.generateRandomText())
              .authorities(authorities)
              .build();

      when(userService.getCurrentUser()).thenReturn(currentUser);

      var assignment = AssignmentConverter.from(request);

      try (MockedStatic<AssignmentConverter> mocked = mockStatic(AssignmentConverter.class)) {
        mocked.when(() -> AssignmentConverter.from(request)).thenReturn(assignment);

        assignmentService.create(request);

        ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepository, times(1)).save(captor.capture());
        reset(assignmentRepository);
        Assignment saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(currentUser.getId(), saved.getOwnerId());
        assertTrue(saved.getIsActive());
      }
    }
  }

  @Test
  void create_shouldThrowForbidden_whenCurrentUserIsStudent() {
    AssignmentRequest request = getGenerateAssignmentRequest();

    long userId = GenerateRandom.generateNumber(1_000_000);
    var authorities = List.of(new SimpleGrantedAuthority(Role.STUDENT.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(userId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.create(request));

    assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    verifyNoInteractions(assignmentRepository);
  }

  private AssignmentRequest getGenerateAssignmentRequest() {

    var time = generateValidTimeRange();

    return AssignmentRequest.builder()
        .title(GenerateRandom.generateRandomText(25))
        .description(GenerateRandom.generateRandomText())
        .subjectName(GenerateRandom.generateRandomText())
        .startTime(time[0])
        .endTime(time[1])
        .build();
  }

  @Test
  void list_shouldReturnPagedAssignments_whenInputIsValid() {
    int n = GenerateRandom.generateNumber(10);
    for (int i = 0; i < n; ++i) {
      int page = GenerateRandom.generateNumber(3) - 1;
      int size = GenerateRandom.generateNumber(20);

      int itemCount = GenerateRandom.generateNumber(size);
      List<Assignment> assignments =
          IntStream.range(0, itemCount)
              .mapToObj(
                  idx ->
                      Assignment.builder()
                          .id((long) GenerateRandom.generateNumber(1_000_000))
                          .title(GenerateRandom.generateRandomText(20))
                          .description(GenerateRandom.generateRandomText(40))
                          .subjectName(GenerateRandom.generateRandomText(10))
                          .startTime(
                              Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .updated(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .isActive(true)
                          .build())
              .toList();

      long totalItems = assignments.size();

      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updated"));
      Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, totalItems);

      when(assignmentRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(assignmentPage);

      List<AssignmentResponse> responses =
          assignments.stream()
              .map(
                  a ->
                      AssignmentResponse.builder()
                          .id(a.getId())
                          .title(a.getTitle())
                          .description(a.getDescription())
                          .subjectName(a.getSubjectName())
                          .ownerId(a.getOwnerId())
                          .startTime(a.getStartTime())
                          .endTime(a.getEndTime())
                          .isActive(a.getIsActive())
                          .created(a.getCreated())
                          .updated(a.getUpdated())
                          .build())
              .toList();

      try (MockedStatic<AssignmentConverter> mocked = mockStatic(AssignmentConverter.class)) {
        mocked.when(() -> AssignmentConverter.to(assignments)).thenReturn(responses);

        PageResponse<AssignmentResponse> result = assignmentService.list(page, size);

        assertNotNull(result);
        assertEquals(page, result.page());
        assertEquals(size, result.size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository, times(1)).findByIsActiveTrue(pageableCaptor.capture());
        Pageable actualPageable = pageableCaptor.getValue();
        assertEquals(page, actualPageable.getPageNumber());
        assertEquals(size, actualPageable.getPageSize());

        reset(assignmentRepository);
      }
    }
  }

  @Test
  void list_shouldThrowInvalidRequest_whenPageIsNegative() {
    int page = -GenerateRandom.generateNumber(5);
    int size = GenerateRandom.generateNumber(20);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.list(page, size));

    assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void list_shouldThrowInvalidRequest_whenSizeIsZeroOrGreaterThan20() {
    int page = 0;

    int sizeZero = 0;
    ResourceException ex1 =
        assertThrows(ResourceException.class, () -> assignmentService.list(page, sizeZero));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    int sizeTooLarge = 20 + GenerateRandom.generateNumber(10);
    ResourceException ex2 =
        assertThrows(ResourceException.class, () -> assignmentService.list(page, sizeTooLarge));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void searchByTitle_shouldReturnPagedAssignments_whenInputIsValid() {
    int n = GenerateRandom.generateNumber(10);
    for (int i = 0; i < n; ++i) {
      int page = GenerateRandom.generateNumber(3) - 1;
      int size = GenerateRandom.generateNumber(20);
      String keyword = GenerateRandom.generateRandomText(10);
      String trimmedKeyword = keyword.trim();

      int itemCount = GenerateRandom.generateNumber(size);
      List<Assignment> assignments =
          IntStream.range(0, itemCount)
              .mapToObj(
                  idx ->
                      Assignment.builder()
                          .id((long) GenerateRandom.generateNumber(1_000_000))
                          .title("title-" + trimmedKeyword + "-" + idx)
                          .description(GenerateRandom.generateRandomText(40))
                          .subjectName(GenerateRandom.generateRandomText(10))
                          .startTime(
                              Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .updated(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                          .isActive(true)
                          .build())
              .toList();

      long totalItems = assignments.size();

      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updated"));
      Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, totalItems);

      when(assignmentRepository.findByIsActiveTrueAndTitleContainingIgnoreCase(
              eq(trimmedKeyword), any(Pageable.class)))
          .thenReturn(assignmentPage);

      List<AssignmentResponse> responses =
          assignments.stream()
              .map(
                  a ->
                      AssignmentResponse.builder()
                          .id(a.getId())
                          .title(a.getTitle())
                          .description(a.getDescription())
                          .subjectName(a.getSubjectName())
                          .ownerId(a.getOwnerId())
                          .startTime(a.getStartTime())
                          .endTime(a.getEndTime())
                          .isActive(a.getIsActive())
                          .created(a.getCreated())
                          .updated(a.getUpdated())
                          .build())
              .toList();

      try (MockedStatic<AssignmentConverter> mocked = mockStatic(AssignmentConverter.class)) {
        mocked.when(() -> AssignmentConverter.to(assignments)).thenReturn(responses);

        PageResponse<AssignmentResponse> result = assignmentService.search(page, size, keyword);

        assertNotNull(result);
        assertEquals(page, result.page());
        assertEquals(size, result.size());
        assertEquals(responses.size(), result.items().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository, times(1))
            .findByIsActiveTrueAndTitleContainingIgnoreCase(
                eq(trimmedKeyword), pageableCaptor.capture());
        Pageable actualPageable = pageableCaptor.getValue();
        assertEquals(page, actualPageable.getPageNumber());
        assertEquals(size, actualPageable.getPageSize());

        reset(assignmentRepository);
      }
    }
  }

  @Test
  void searchByTitle_shouldThrowInvalidRequest_whenKeywordIsNullOrBlank() {
    int page = 0;
    int size = GenerateRandom.generateNumber(20);

    ResourceException ex1 =
        assertThrows(ResourceException.class, () -> assignmentService.search(page, size, null));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    ResourceException ex2 =
        assertThrows(ResourceException.class, () -> assignmentService.search(page, size, "   "));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void searchByTitle_shouldThrowInvalidRequest_whenPageOrSizeInvalid() {
    int size = GenerateRandom.generateNumber(20);
    String keyword = GenerateRandom.generateRandomText(10);

    int invalidPage = -GenerateRandom.generateNumber(5);
    ResourceException ex1 =
        assertThrows(
            ResourceException.class, () -> assignmentService.search(invalidPage, size, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    int page = 0;
    int sizeZero = 0;
    ResourceException ex2 =
        assertThrows(
            ResourceException.class, () -> assignmentService.search(page, sizeZero, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    int sizeTooLarge = 20 + GenerateRandom.generateNumber(10);
    ResourceException ex3 =
        assertThrows(
            ResourceException.class, () -> assignmentService.search(page, sizeTooLarge, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex3.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }

  private AssignmentUpdateRequest randomUpdateRequest(Long id) {
    Timestamp[] range = generateValidTimeRange();

    return new AssignmentUpdateRequest(
        id,
        GenerateRandom.generateRandomText(20),
        GenerateRandom.generateRandomText(40),
        GenerateRandom.generateRandomText(10),
        range[0],
        range[1],
        GenerateRandom.generateNumber(2) == 1);
  }

  private Timestamp[] generateValidTimeRange() {
    var start = GenerateRandom.generateRandomLocalDateTime();
    var end = start.plusMinutes(GenerateRandom.generateNumber(180));
    return new Timestamp[] {Timestamp.valueOf(start), Timestamp.valueOf(end)};
  }

  @Test
  void update_shouldUpdateAssignment_whenOwnerAndValidRequest() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);

    Assignment existing =
        Assignment.builder()
            .id(assignmentId)
            .title(GenerateRandom.generateRandomText(20))
            .description(GenerateRandom.generateRandomText(40))
            .subjectName(GenerateRandom.generateRandomText(10))
            .ownerId(ownerId)
            .startTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .isActive(true)
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(existing));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(ownerId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    AssignmentUpdateRequest request = randomUpdateRequest(assignmentId);

    assignmentService.update(request);

    ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
    verify(assignmentRepository).save(captor.capture());

    Assignment updated = captor.getValue();
    assertEquals(request.title(), updated.getTitle());
    assertEquals(request.description(), updated.getDescription());
    assertEquals(request.subjectName(), updated.getSubjectName());
    assertEquals(request.startTime(), updated.getStartTime());
    assertEquals(request.endTime(), updated.getEndTime());
    assertEquals(request.isActive(), updated.getIsActive());
  }

  @Test
  void update_shouldAllowAdminToUpdate_whenAdminNotOwner() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);
    long adminId = ownerId + 100;

    Assignment existing =
        Assignment.builder()
            .id(assignmentId)
            .ownerId(ownerId)
            .title(GenerateRandom.generateRandomText(20))
            .isActive(true)
            .startTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(existing));

    var authorities = List.of(new SimpleGrantedAuthority(Role.ADMIN.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(adminId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    AssignmentUpdateRequest request = randomUpdateRequest(assignmentId);

    assignmentService.update(request);

    ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
    verify(assignmentRepository).save(captor.capture());

    Assignment updated = captor.getValue();
    assertEquals(request.title(), updated.getTitle());
  }

  @Test
  void update_shouldThrowNotFound_whenAssignmentDoesNotExist() {
    long id = GenerateRandom.generateNumber(1_000_000);

    when(assignmentRepository.findById(id)).thenReturn(java.util.Optional.empty());

    AssignmentUpdateRequest request = randomUpdateRequest(id);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.update(request));

    assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void update_shouldThrowForbidden_whenNotOwnerOrAdmin() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);
    long otherUserId = ownerId + 123;

    Assignment existing =
        Assignment.builder()
            .id(assignmentId)
            .ownerId(ownerId)
            .isActive(true)
            .title(GenerateRandom.generateRandomText(20))
            .startTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(existing));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(otherUserId)
            .authorities(authorities)
            .username(GenerateRandom.generateRandomText())
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    AssignmentUpdateRequest request = randomUpdateRequest(assignmentId);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.update(request));

    assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void update_shouldThrowForbidden_whenAssignmentIsInactive() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);

    Assignment existing =
        Assignment.builder()
            .id(assignmentId)
            .ownerId(ownerId)
            .isActive(false)
            .title(GenerateRandom.generateRandomText(20))
            .startTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(existing));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(ownerId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    AssignmentUpdateRequest request = randomUpdateRequest(assignmentId);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.update(request));

    assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void update_shouldThrowInvalidRequest_whenEndTimeBeforeStartTime() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);
    var time = generateValidTimeRange();
    Assignment existing =
        Assignment.builder()
            .id(assignmentId)
            .ownerId(ownerId)
            .isActive(true)
            .title(GenerateRandom.generateRandomText(20))
            .startTime(time[1])
            .endTime(time[0])
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(existing));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(ownerId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);
    AssignmentUpdateRequest request =
        new AssignmentUpdateRequest(
            assignmentId,
            GenerateRandom.generateRandomText(20),
            GenerateRandom.generateRandomText(40),
            GenerateRandom.generateRandomText(10),
            time[1],
            time[0],
            true);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.update(request));

    assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void delete_shouldSoftDeleteAssignment_whenOwnerAndActive() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);

    Timestamp[] range = generateValidTimeRange();

    Assignment assignment =
        Assignment.builder()
            .id(assignmentId)
            .title(GenerateRandom.generateRandomText(20))
            .description(GenerateRandom.generateRandomText(40))
            .subjectName(GenerateRandom.generateRandomText(10))
            .ownerId(ownerId)
            .startTime(range[0])
            .endTime(range[1])
            .isActive(true)
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(assignment));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(ownerId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    assignmentService.delete(assignmentId);

    ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
    verify(assignmentRepository, times(1)).save(captor.capture());
    Assignment deleted = captor.getValue();
    assertEquals(assignmentId, deleted.getId());
    assertFalse(deleted.getIsActive());
  }

  @Test
  void delete_shouldAllowAdminToSoftDelete_whenAdminNotOwnerAndActive() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);
    long adminId = ownerId + 123;

    Timestamp[] range = generateValidTimeRange();

    Assignment assignment =
        Assignment.builder()
            .id(assignmentId)
            .title(GenerateRandom.generateRandomText(20))
            .description(GenerateRandom.generateRandomText(40))
            .subjectName(GenerateRandom.generateRandomText(10))
            .ownerId(ownerId)
            .startTime(range[0])
            .endTime(range[1])
            .isActive(true)
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(assignment));

    var authorities = List.of(new SimpleGrantedAuthority(Role.ADMIN.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(adminId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    assignmentService.delete(assignmentId);

    ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
    verify(assignmentRepository, times(1)).save(captor.capture());
    Assignment deleted = captor.getValue();
    assertEquals(assignmentId, deleted.getId());
    assertFalse(deleted.getIsActive());
  }

  @Test
  void delete_shouldThrowNotFound_whenAssignmentDoesNotExist() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.empty());

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.delete(assignmentId));

    assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void delete_shouldThrowForbidden_whenCurrentUserIsNotOwnerOrAdmin() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);
    long otherUserId = ownerId + 999;

    Timestamp[] range = generateValidTimeRange();

    Assignment assignment =
        Assignment.builder()
            .id(assignmentId)
            .title(GenerateRandom.generateRandomText(20))
            .description(GenerateRandom.generateRandomText(40))
            .subjectName(GenerateRandom.generateRandomText(10))
            .ownerId(ownerId)
            .startTime(range[0])
            .endTime(range[1])
            .isActive(true)
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(assignment));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(otherUserId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.delete(assignmentId));

    assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }

  @Test
  void delete_shouldThrowForbidden_whenAssignmentIsInactive() {
    long assignmentId = GenerateRandom.generateNumber(1_000_000);
    long ownerId = GenerateRandom.generateNumber(1_000_000);

    Timestamp[] range = generateValidTimeRange();

    Assignment assignment =
        Assignment.builder()
            .id(assignmentId)
            .title(GenerateRandom.generateRandomText(20))
            .description(GenerateRandom.generateRandomText(40))
            .subjectName(GenerateRandom.generateRandomText(10))
            .ownerId(ownerId)
            .startTime(range[0])
            .endTime(range[1])
            .isActive(false)
            .build();

    when(assignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.of(assignment));

    var authorities = List.of(new SimpleGrantedAuthority(Role.TEACHER.getAuthority()));
    var currentUser =
        AuthenticatedUser.builder()
            .id(ownerId)
            .username(GenerateRandom.generateRandomText())
            .authorities(authorities)
            .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);

    ResourceException ex =
        assertThrows(ResourceException.class, () -> assignmentService.delete(assignmentId));

    assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    verify(assignmentRepository, never()).save(any(Assignment.class));
  }
}
