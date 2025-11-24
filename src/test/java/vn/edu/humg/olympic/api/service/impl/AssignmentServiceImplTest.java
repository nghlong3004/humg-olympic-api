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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import vn.edu.humg.olympic.api.converter.AssignmentConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.CustomUserDetails;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
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
          CustomUserDetails.builder()
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
      }
    }
  }

  @Test
  void create_shouldThrowForbidden_whenCurrentUserIsStudent() {
    AssignmentRequest request = getGenerateAssignmentRequest();

    long userId = GenerateRandom.generateNumber(1_000_000);
    var authorities = List.of(new SimpleGrantedAuthority(Role.STUDENT.getAuthority()));
    var currentUser =
        CustomUserDetails.builder()
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
    return AssignmentRequest.builder()
        .title(GenerateRandom.generateRandomText(25))
        .description(GenerateRandom.generateRandomText())
        .subjectName(GenerateRandom.generateRandomText())
        .startTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
        .endTime(Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
        .build();
  }

  @Test
  void list_shouldReturnPagedAssignments_whenInputIsValid() {
    int n = GenerateRandom.generateNumber(10);
    for (int i = 0; i < n; ++i) {
      int page = GenerateRandom.generateNumber(3) - 1; 
      int size = GenerateRandom.generateNumber(20);
      int offset = page * size;

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
                                              .endTime(
                                                      Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                                              .updated(
                                                      Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                                              .build())
                      .toList();

      long totalItems = GenerateRandom.generateNumber(200);

      when(assignmentRepository.findAllPaging(offset, size)).thenReturn(assignments);
      when(assignmentRepository.countAll()).thenReturn(totalItems);

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
        assertEquals(totalItems, result.totalItems());
        int expectedTotalPages = (int) Math.ceil((double) totalItems / size);
        assertEquals(expectedTotalPages, result.totalPages());
        assertEquals(responses.size(), result.items().size());

        verify(assignmentRepository, times(1)).findAllPaging(offset, size);
        verify(assignmentRepository, times(1)).countAll();

        reset(assignmentRepository);
      }
    }
  }

  @Test
  void list_shouldThrowInvalidRequest_whenPageIsNegative() {
    int page = -GenerateRandom.generateNumber(5);
    int size = GenerateRandom.generateNumber(20);

    ResourceException ex = assertThrows(ResourceException.class, () -> assignmentService.list(page, size));

    assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void list_shouldThrowInvalidRequest_whenSizeIsZeroOrGreaterThan20() {
    int page = 0;

    int sizeZero = 0;
    ResourceException ex1 = assertThrows(ResourceException.class, () -> assignmentService.list(page, sizeZero));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    int sizeTooLarge = 20 + GenerateRandom.generateNumber(10);
    ResourceException ex2 = assertThrows(ResourceException.class, () -> assignmentService.list(page, sizeTooLarge));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void searchByTitle_shouldReturnPagedAssignments_whenInputIsValid() {
    int n = GenerateRandom.generateNumber(10);
    for (int i = 0; i < n; ++i) {
      int page = GenerateRandom.generateNumber(3) - 1;
      int size = GenerateRandom.generateNumber(20);
      int offset = page * size;
      String keyword = GenerateRandom.generateRandomText(10);

      int itemCount = GenerateRandom.generateNumber(size);
      List<Assignment> assignments =
              IntStream.range(0, itemCount)
                      .mapToObj(
                              idx ->
                                      Assignment.builder()
                                              .id((long) GenerateRandom.generateNumber(1_000_000))
                                              .title("title-" + keyword + "-" + idx)
                                              .description(GenerateRandom.generateRandomText(40))
                                              .subjectName(GenerateRandom.generateRandomText(10))
                                              .startTime(
                                                      Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                                              .endTime(
                                                      Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                                              .updated(
                                                      Timestamp.valueOf(GenerateRandom.generateRandomLocalDateTime()))
                                              .build())
                      .toList();

      long totalItems = GenerateRandom.generateNumber(100);

      when(assignmentRepository.searchByTitlePaging(offset, size, keyword))
              .thenReturn(assignments);
      when(assignmentRepository.countByTitle(keyword)).thenReturn(totalItems);

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

        PageResponse<AssignmentResponse> result =
                assignmentService.searchByTitle(page, size, keyword);

        assertNotNull(result);
        assertEquals(page, result.page());
        assertEquals(size, result.size());
        assertEquals(totalItems, result.totalItems());
        int expectedTotalPages = (int) Math.ceil((double) totalItems / size);
        assertEquals(expectedTotalPages, result.totalPages());
        assertEquals(responses.size(), result.items().size());

        verify(assignmentRepository, times(1))
                .searchByTitlePaging(offset, size, keyword);
        verify(assignmentRepository, times(1))
                .countByTitle(keyword);

        reset(assignmentRepository);
      }
    }
  }

  @Test
  void searchByTitle_shouldThrowInvalidRequest_whenKeywordIsNullOrBlank() {
    int page = 0;
    int size = GenerateRandom.generateNumber(20);

    ResourceException ex1 =
            assertThrows(ResourceException.class, () -> assignmentService.searchByTitle(page, size, null));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    ResourceException ex2 =
            assertThrows(ResourceException.class, () -> assignmentService.searchByTitle(page, size, "   "));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void searchByTitle_shouldThrowInvalidRequest_whenPageOrSizeInvalid() {
    int size = GenerateRandom.generateNumber(20);
    String keyword = GenerateRandom.generateRandomText(10);

    int invalidPage = -GenerateRandom.generateNumber(5);
    ResourceException ex1 =
            assertThrows(ResourceException.class, () -> assignmentService.searchByTitle(invalidPage, size, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex1.getErrorCode());

    int page = 0;
    int sizeZero = 0;
    ResourceException ex2 =
            assertThrows(ResourceException.class, () -> assignmentService.searchByTitle(page, sizeZero, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex2.getErrorCode());

    int sizeTooLarge = 20 + GenerateRandom.generateNumber(10);
    ResourceException ex3 =
            assertThrows(ResourceException.class, () -> assignmentService.searchByTitle(page, sizeTooLarge, keyword));
    assertEquals(ErrorCode.INVALID_REQUEST, ex3.getErrorCode());

    verifyNoInteractions(assignmentRepository);
  }
}
