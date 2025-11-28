package vn.edu.humg.olympic.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import vn.edu.humg.olympic.api.constant.JwtConstant;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.UserUpdateRequest;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.util.GenerateRandom;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks @Spy private UserServiceImpl userService;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getUser_shouldReturnUserResponse_whenUserExists() {
    int n = GenerateRandom.generateNumber(15);
    for (int i = 0; i < n; ++i) {
      User userInDatabase = generateUser();
      Long id = userInDatabase.getId();
      when(userRepository.findById(id)).thenReturn(Optional.of(userInDatabase));

      var user = userService.getUser(id);

      assertNotNull(user);
      Assertions.assertEquals(userInDatabase.getId(), user.id());
      Assertions.assertEquals(userInDatabase.getEmail(), user.email());
      Assertions.assertEquals(userInDatabase.getCreated(), user.created());
      Assertions.assertEquals(userInDatabase.getFirstName(), user.firstName());
      Assertions.assertEquals(userInDatabase.getLastName(), user.lastName());
      Assertions.assertEquals(userInDatabase.getGender(), user.gender());
      Assertions.assertEquals(userInDatabase.getRole(), user.role());
      Assertions.assertEquals(userInDatabase.getFacultyName(), user.facultyName());
    }
  }

  @Test
  void getUser_shouldThrowNotFound_whenUserDoesNotExist() {
    Long id = 999L;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    ResourceException exception =
        assertThrows(ResourceException.class, () -> userService.getUser(id));

    Assertions.assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void getCurrentUser_shouldReturnAuthenticatedUser_fromSecurityContext() {
    Long userId = (long) GenerateRandom.generateNumber(1_000_000);
    String username = GenerateRandom.generateRandomEmail();
    String role = GenerateRandom.generateAuthority();

    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaim(JwtConstant.USER_ID)).thenReturn(userId);
    when(jwt.getClaimAsString(JwtConstant.SCOPE)).thenReturn(role);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(jwt);
    when(authentication.getName()).thenReturn(username);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    var currentUser = userService.getCurrentUser();

    Assertions.assertEquals(userId, currentUser.getId());
    Assertions.assertEquals(username, currentUser.getUsername());
    Assertions.assertTrue(
        currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)));
  }

  @Test
  void update_shouldUpdateUser_whenCurrentUserIsOwner() {
    Long userId = (long) GenerateRandom.generateNumber(1_000_000);

    AuthenticatedUser authenticatedUser =
        AuthenticatedUser.builder()
            .id(userId)
            .username(GenerateRandom.generateRandomEmail())
            .authorities(List.of(new SimpleGrantedAuthority(GenerateRandom.generateAuthority())))
            .build();

    User existing = generateUser();
    existing.setId(userId);

    doReturn(authenticatedUser).when(userService).getCurrentUser();

    User randomUser = generateUser();

    UserUpdateRequest request = mock(UserUpdateRequest.class);
    when(request.id()).thenReturn(userId);
    when(request.firstName()).thenReturn(randomUser.getFirstName());
    when(request.lastName()).thenReturn(randomUser.getLastName());
    when(request.phone()).thenReturn(randomUser.getPhone());
    when(request.universityName()).thenReturn(randomUser.getUniversityName());
    when(request.facultyName()).thenReturn(randomUser.getFacultyName());
    when(request.avatarUrl()).thenReturn(GenerateRandom.generateRandomText());
    when(request.gender()).thenReturn(randomUser.getGender());
    when(request.role()).thenReturn(randomUser.getRole());
    when(request.isActive()).thenReturn(randomUser.getIsActive());
    when(request.birthday()).thenReturn(randomUser.getBirthday());

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    userService.update(request);

    verify(userRepository).save(userCaptor.capture());
    User updatedUser = userCaptor.getValue();

    Assertions.assertEquals(userId, updatedUser.getId());
    Assertions.assertEquals(randomUser.getFirstName(), updatedUser.getFirstName());
    Assertions.assertEquals(randomUser.getLastName(), updatedUser.getLastName());
    Assertions.assertEquals(randomUser.getPhone(), updatedUser.getPhone());
    Assertions.assertEquals(randomUser.getUniversityName(), updatedUser.getUniversityName());
    Assertions.assertEquals(randomUser.getFacultyName(), updatedUser.getFacultyName());
    Assertions.assertEquals(randomUser.getGender(), updatedUser.getGender());
    Assertions.assertEquals(randomUser.getRole(), updatedUser.getRole());
    Assertions.assertEquals(randomUser.getIsActive(), updatedUser.getIsActive());
    Assertions.assertEquals(randomUser.getBirthday(), updatedUser.getBirthday());
  }

  @Test
  void update_shouldThrowForbidden_whenNotOwnerAndNotAdmin() {
    Long currentUserId = (long) GenerateRandom.generateNumber(1_000_000);
    Long targetUserId = (long) GenerateRandom.generateNumber(1_000_000);

    AuthenticatedUser authenticatedUser =
        AuthenticatedUser.builder()
            .id(currentUserId)
            .username(GenerateRandom.generateRandomEmail())
            .authorities(List.of(new SimpleGrantedAuthority(Role.STUDENT.getAuthority())))
            .build();

    doReturn(authenticatedUser).when(userService).getCurrentUser();

    UserUpdateRequest request = mock(UserUpdateRequest.class);
    when(request.id()).thenReturn(targetUserId);

    ResourceException exception =
        assertThrows(ResourceException.class, () -> userService.update(request));

    Assertions.assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    verify(userRepository, never()).save(any(User.class));
    verify(userRepository, never()).findById(anyLong());
  }

  @Test
  void search_shouldReturnEmptyPage_whenNoUserFound() {
    int page = GenerateRandom.generateNumber(4) - 1;
    int size = GenerateRandom.generateNumber(10);
    String keyword = GenerateRandom.generateRandomText(5);
    Role role = Role.STUDENT;

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
    Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    when(userRepository.search(eq("%" + keyword + "%"), eq(role.name()), any(Pageable.class)))
        .thenReturn(emptyPage);

    PageResponse<?> response = userService.search(page, size, keyword, role);

    Assertions.assertEquals(page, response.page());
    Assertions.assertEquals(size, response.size());
    Assertions.assertEquals(0, response.totalItem());
    Assertions.assertEquals(0, response.totalPage());
    Assertions.assertTrue(response.items().isEmpty());

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(userRepository)
        .search(eq("%" + keyword + "%"), eq(role.name()), pageableCaptor.capture());

    Pageable actualPageable = pageableCaptor.getValue();
    Assertions.assertEquals(page, actualPageable.getPageNumber());
    Assertions.assertEquals(size, actualPageable.getPageSize());
  }

  @Test
  void search_shouldReturnProperPageResponse_whenUsersExist() {
    int page = 1;
    int size = 10;
    String keyword = GenerateRandom.generateRandomText(6);
    Role role = Role.TEACHER;

    User u1 = generateUser();
    User u2 = generateUser();
    List<User> list = List.of(u1, u2);

    long total = 37L;

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
    Page<User> userPage = new PageImpl<>(list, pageable, total);

    when(userRepository.search(eq("%" + keyword + "%"), eq(role.name()), any(Pageable.class)))
        .thenReturn(userPage);

    PageResponse<?> response = userService.search(page, size, keyword, role);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(userRepository)
        .search(eq("%" + keyword + "%"), eq(role.name()), pageableCaptor.capture());

    Pageable actualPageable = pageableCaptor.getValue();
    Assertions.assertEquals(page, actualPageable.getPageNumber());
    Assertions.assertEquals(size, actualPageable.getPageSize());

    Assertions.assertEquals(page, response.page());
    Assertions.assertEquals(size, response.size());
    Assertions.assertEquals(total, response.totalItem());
    Assertions.assertEquals((int) Math.ceil((double) total / size), response.totalPage());
    Assertions.assertEquals(2, response.items().size());
  }

  private User generateUser() {
    return User.builder()
        .id((long) GenerateRandom.generateNumber(1_000_000))
        .firstName(GenerateRandom.generateRandomText(20))
        .lastName(GenerateRandom.generateRandomText(20))
        .email(GenerateRandom.generateRandomEmail())
        .role(GenerateRandom.generateRole())
        .gender(GenerateRandom.generateGender())
        .phone(GenerateRandom.generateRandomVNPhoneNumber())
        .universityName(GenerateRandom.generateRandomText(20))
        .facultyName(GenerateRandom.generateRandomText(20))
        .birthday(Date.valueOf(GenerateRandom.generateRandomLocalDate()))
        .avatarUrl(GenerateRandom.generateRandomText())
        .created(GenerateRandom.generateRandomTimestamp())
        .updated(GenerateRandom.generateRandomTimestamp())
        .isActive(true)
        .build();
  }
}
