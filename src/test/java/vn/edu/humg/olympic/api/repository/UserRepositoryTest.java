package vn.edu.humg.olympic.api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import java.sql.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void save_and_findByEmail_shouldWorkWithDatabase() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String email = generateRandomEmail();

      User user =
          User.builder()
              .firstName(generateRandomText(24))
              .lastName(generateRandomText(24))
              .email(email)
              .passwordHash(generateRandomText())
              .gender(generateGender())
              .birthday(Date.valueOf(generateRandomLocalDate()))
              .role(Role.STUDENT)
              .phone(generateRandomVNPhoneNumber())
              .universityName(generateRandomText(24))
              .facultyName(generateRandomText(24))
              .avatarUrl(null)
              .isActive(true)
              .build();

      userRepository.save(user);

      Optional<User> foundOpt = userRepository.findByEmail(email);

      assertThat(foundOpt).isPresent();
      User found = foundOpt.get();
      assertThat(found.getEmail()).isEqualTo(user.getEmail());
      assertThat(found.getFirstName()).isEqualTo(user.getFirstName());
      assertThat(found.getLastName()).isEqualTo(user.getLastName());
      assertThat(found.getRole()).isEqualTo(user.getRole());
      assertThat(found.getGender()).isEqualTo(user.getGender());
      assertThat(found.getPasswordHash()).isEqualTo(user.getPasswordHash());
      assertThat(found.getUniversityName()).isEqualTo(user.getUniversityName());
      assertThat(found.getFacultyName()).isEqualTo(user.getFacultyName());
      assertThat(found.getIsActive()).isEqualTo(user.getIsActive());
    }
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenNotExists() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String email = generateRandomEmail();

      Optional<User> foundOpt = userRepository.findByEmail(email);

      assertThat(foundOpt).isEmpty();
    }
  }

  @Test
  void save_and_findById_shouldWorkWithDatabase() {
    int n = generateNumber(10);
    for (int i = 0; i < n; ++i) {
      String email = generateRandomEmail();

      User user =
          User.builder()
              .firstName(generateRandomText(24))
              .lastName(generateRandomText(24))
              .email(email)
              .passwordHash(generateRandomText())
              .gender(generateGender())
              .birthday(Date.valueOf(generateRandomLocalDate()))
              .role(generateRole())
              .phone(generateRandomVNPhoneNumber())
              .universityName(generateRandomText(24))
              .facultyName(generateRandomText(24))
              .avatarUrl(generateRandomText(24))
              .isActive(true)
              .build();

      userRepository.save(user);

      Optional<User> foundByEmailOpt = userRepository.findByEmail(email);
      assertThat(foundByEmailOpt).isPresent();

      User foundByEmail = foundByEmailOpt.get();
      Long id = foundByEmail.getId();

      Optional<User> foundByIdOpt = userRepository.findById(id);
      assertThat(foundByIdOpt).isPresent();

      User foundById = foundByIdOpt.get();
      assertThat(foundById.getId()).isEqualTo(id);
      assertThat(foundById.getEmail()).isEqualTo(email);
      assertThat(foundById.getFirstName()).isEqualTo(foundByEmail.getFirstName());
      assertThat(foundById.getLastName()).isEqualTo(foundByEmail.getLastName());
      assertThat(foundById.getRole()).isEqualTo(foundByEmail.getRole());
      assertThat(foundById.getGender()).isEqualTo(foundByEmail.getGender());
      assertThat(foundById.getPhone()).isEqualTo(foundByEmail.getPhone());
    }
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    int n = generateNumber(10);
    for (int i = 0; i < n; ++i) {
      long randomId = generateNumber(1_000_000);

      Optional<User> foundOpt = userRepository.findById(randomId);

      assertThat(foundOpt).isEmpty();
    }
  }

  @Test
  void update_shouldUpdateExistingUser() {
    String email = generateRandomEmail();

    User user =
        User.builder()
            .firstName(generateRandomText(24))
            .lastName(generateRandomText(24))
            .email(email)
            .passwordHash(generateRandomText())
            .gender(generateGender())
            .birthday(Date.valueOf(generateRandomLocalDate()))
            .role(generateRole())
            .phone(generateRandomVNPhoneNumber())
            .universityName(generateRandomText(24))
            .facultyName(generateRandomText(24))
            .avatarUrl(generateRandomText(24))
            .isActive(true)
            .build();

    userRepository.save(user);

    User existing = userRepository.findByEmail(email).orElseThrow();
    Long id = existing.getId();

    String newFirstName = generateRandomText(10);
    String newLastName = generateRandomText(10);
    String newPhone = generateRandomVNPhoneNumber();
    String newUniversity = generateRandomText(15);
    String newFaculty = generateRandomText(15);
    String newAvatar = generateRandomText(20);
    boolean newIsActive = false;

    User updatedUser =
        User.builder()
            .id(id)
            .firstName(newFirstName)
            .lastName(newLastName)
            .email(existing.getEmail())
            .passwordHash(existing.getPasswordHash())
            .gender(existing.getGender())
            .birthday(existing.getBirthday())
            .role(existing.getRole())
            .phone(newPhone)
            .universityName(newUniversity)
            .facultyName(newFaculty)
            .avatarUrl(newAvatar)
            .isActive(newIsActive)
            .build();

    userRepository.update(updatedUser);

    Optional<User> foundOpt = userRepository.findById(id);
    assertThat(foundOpt).isPresent();

    User found = foundOpt.get();
    assertThat(found.getId()).isEqualTo(id);
    assertThat(found.getFirstName()).isEqualTo(newFirstName);
    assertThat(found.getLastName()).isEqualTo(newLastName);
    assertThat(found.getPhone()).isEqualTo(newPhone);
    assertThat(found.getUniversityName()).isEqualTo(newUniversity);
    assertThat(found.getFacultyName()).isEqualTo(newFaculty);
    assertThat(found.getAvatarUrl()).isEqualTo(newAvatar);
    assertThat(found.getIsActive()).isEqualTo(newIsActive);
    assertThat(found.getEmail()).isEqualTo(existing.getEmail());
    assertThat(found.getPasswordHash()).isEqualTo(existing.getPasswordHash());
    assertThat(found.getGender()).isEqualTo(existing.getGender());
    assertThat(found.getRole()).isEqualTo(existing.getRole());
  }
}
