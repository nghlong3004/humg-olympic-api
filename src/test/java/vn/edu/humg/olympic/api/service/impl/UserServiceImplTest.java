package vn.edu.humg.olympic.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.util.GenerateRandom;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 11/26/2025
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceImpl userService;

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
        .build();
  }
}
