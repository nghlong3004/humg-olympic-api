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
}
