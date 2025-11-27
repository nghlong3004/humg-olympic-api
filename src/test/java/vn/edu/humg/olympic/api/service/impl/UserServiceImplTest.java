package vn.edu.humg.olympic.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
  void getUser_shouldReturnUserResponse_whenTokenValid() {
    int n = GenerateRandom.generateNumber(15);
    for (int i = 0; i < n; ++i) {
      User userInDatabase = generateUser();
      Long id = userInDatabase.getId();
      Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userInDatabase));

      var user = userService.getUser(id);

      assertNotNull(user);
      Assertions.assertEquals(user.id(), userInDatabase.getId());
      Assertions.assertEquals(user.email(), userInDatabase.getEmail());
      Assertions.assertEquals(user.created(), userInDatabase.getCreated());
      Assertions.assertEquals(user.firstName(), userInDatabase.getFirstName());
      Assertions.assertEquals(user.lastName(), userInDatabase.getLastName());
      Assertions.assertEquals(user.gender(), userInDatabase.getGender());
      Assertions.assertEquals(user.role(), userInDatabase.getRole());
      Assertions.assertEquals(user.facultyName(), userInDatabase.getFacultyName());
    }
  }

  @Test
  void getUser_shouldThrowNotFound_whenUserDoesNotExist() {
    Long id = 999L;
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

    ResourceException exception =
            assertThrows(ResourceException.class, () -> userService.getUser(id));

    Assertions.assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void getCurrentUser_shouldReturnAuthenticatedUser_fromSecurityContext() {
    Long userId = (long) GenerateRandom.generateNumber(1_000_000);
    String username = GenerateRandom.generateRandomEmail();
    String role = GenerateRandom.generateAuthority();

    Jwt jwt = Mockito.mock(Jwt.class);
    Mockito.when(jwt.getClaim(JwtConstant.USER_ID)).thenReturn(userId);
    Mockito.when(jwt.getClaimAsString(JwtConstant.SCOPE)).thenReturn(role);

    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getPrincipal()).thenReturn(jwt);
    Mockito.when(authentication.getName()).thenReturn(username);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
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

    User randomUser = generateUser();

    Mockito.doReturn(authenticatedUser).when(userService).getCurrentUser();

    UserUpdateRequest request = Mockito.mock(UserUpdateRequest.class);
    Mockito.when(request.id()).thenReturn(userId);
    Mockito.when(request.firstName()).thenReturn(randomUser.getFirstName());
    Mockito.when(request.lastName()).thenReturn(randomUser.getLastName());
    Mockito.when(request.phone()).thenReturn(randomUser.getPhone());
    Mockito.when(request.universityName()).thenReturn(randomUser.getUniversityName());
    Mockito.when(request.facultyName()).thenReturn(randomUser.getFacultyName());
    Mockito.when(request.avatarUrl()).thenReturn(GenerateRandom.generateRandomText());
    Mockito.when(request.gender()).thenReturn(randomUser.getGender());
    Mockito.when(request.role()).thenReturn(randomUser.getRole());
    Mockito.when(request.isActive()).thenReturn(randomUser.getIsActive());
    Mockito.when(request.birthday()).thenReturn(randomUser.getBirthday());

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    userService.update(request);

    Mockito.verify(userRepository).update(userCaptor.capture());
    User updatedUser = userCaptor.getValue();

    Assertions.assertEquals(userId, updatedUser.getId());
    Assertions.assertEquals(randomUser.getFirstName(), updatedUser.getFirstName());
    Assertions.assertEquals(randomUser.getLastName(), updatedUser.getLastName());
    Assertions.assertEquals(randomUser.getPhone(), updatedUser.getPhone());
    Assertions.assertEquals(randomUser.getUniversityName(), updatedUser.getUniversityName());
    Assertions.assertEquals(randomUser.getFacultyName(), updatedUser.getFacultyName());
    Assertions.assertEquals(randomUser.getGender(), updatedUser.getGender());
    Assertions.assertEquals(randomUser.getRole(), updatedUser.getRole());
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

    Mockito.doReturn(authenticatedUser).when(userService).getCurrentUser();

    UserUpdateRequest request = Mockito.mock(UserUpdateRequest.class);
    Mockito.when(request.id()).thenReturn(targetUserId);

    ResourceException exception =
            assertThrows(ResourceException.class, () -> userService.update(request));

    Assertions.assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    Mockito.verify(userRepository, Mockito.never()).update(Mockito.any(User.class));
  }

  @Test
  void search_shouldReturnEmptyPage_whenNoUserFound() {
    int page = GenerateRandom.generateNumber(4) - 1;
    int size = GenerateRandom.generateNumber(10);
    String keyword = GenerateRandom.generateRandomText(5);
    Role role = Role.STUDENT;

    Mockito.when(userRepository.countByKeywordAndRole(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(0);

    PageResponse<?> response = userService.search(page, size, keyword, role);

    Assertions.assertEquals(page, response.page());
    Assertions.assertEquals(size, response.size());
    Assertions.assertEquals(0, response.totalItem());
    Assertions.assertEquals(0, response.totalPage());
    Assertions.assertTrue(response.items().isEmpty());
  }

  @Test
  void search_shouldReturnProperPageResponse_whenUsersExist() {
    int page = 1;
    int size = 10;
    String keyword = GenerateRandom.generateRandomText(6);
    Role role = Role.TEACHER;

    int total = 37;

    Mockito.when(userRepository.countByKeywordAndRole(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(total);

    User u1 = generateUser();
    User u2 = generateUser();
    List<User> list = List.of(u1, u2);

    Mockito.when(
                    userRepository.search(
                            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(list);

    PageResponse<?> response = userService.search(page, size, keyword, role);

    ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);

    Mockito.verify(userRepository)
            .search(
                    offsetCaptor.capture(),
                    limitCaptor.capture(),
                    keywordCaptor.capture(),
                    roleCaptor.capture());

    int expectedOffset = page * size;
    String expectedKeyword = "%" + keyword + "%";
    String expectedRole = "%" + role.name() + "%";

    Assertions.assertEquals(expectedOffset, offsetCaptor.getValue());
    Assertions.assertEquals(size, limitCaptor.getValue());
    Assertions.assertEquals(expectedKeyword, keywordCaptor.getValue());
    Assertions.assertEquals(expectedRole, roleCaptor.getValue());

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
