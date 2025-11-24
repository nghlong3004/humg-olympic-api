package vn.edu.humg.olympic.api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.humg.olympic.api.model.Assignment;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AssignmentRepositoryTest {

  @Autowired private AssignmentRepository assignmentRepository;

  @Autowired private UserRepository userRepository;

  private User createRandomUser() {
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
    return userRepository.findByEmail(email).orElseThrow();
  }

  private Assignment buildAssignment(User owner) {
    return Assignment.builder()
        .title(generateRandomText(18))
        .description(generateRandomText(40))
        .subjectName(generateRandomText(10))
        .ownerId(owner.getId())
        .startTime(Timestamp.valueOf(generateRandomLocalDateTime()))
        .endTime(Timestamp.valueOf(generateRandomLocalDateTime()))
        .build();
  }

  @Test
  void save_countAll_and_findAllPaging_shouldWorkWithDatabase() {
    User owner = createRandomUser();

    long oldCount = assignmentRepository.countAll();
    int toInsert = generateNumber(5) + 3;

    for (int i = 0; i < toInsert; i++) {
      assignmentRepository.save(buildAssignment(owner));
    }

    long newCount = assignmentRepository.countAll();
    assertThat(newCount - oldCount).isEqualTo(toInsert);

    List<Assignment> page = assignmentRepository.findAllPaging(0, (int) newCount);
    assertThat(page).isNotEmpty();
  }

  @Test
  void findAllPaging_shouldRespectOffsetAndLimit_withoutOverlap() {
    User owner = createRandomUser();

    int totalInsert = 7;
    for (int i = 0; i < totalInsert; i++) {
      assignmentRepository.save(buildAssignment(owner));
    }

    int pageSize = 3;

    List<Assignment> firstPage = assignmentRepository.findAllPaging(0, pageSize);
    List<Assignment> secondPage = assignmentRepository.findAllPaging(pageSize, pageSize);

    assertThat(firstPage).hasSize(pageSize);
    assertThat(secondPage).hasSize(pageSize);

    Set<Long> firstIds = firstPage.stream().map(Assignment::getId).collect(Collectors.toSet());
    Set<Long> secondIds = secondPage.stream().map(Assignment::getId).collect(Collectors.toSet());

    assertThat(firstIds).doesNotContainAnyElementsOf(secondIds);
  }

  @Test
  void findAllPaging_shouldReturnEmpty_whenOffsetTooLarge() {
    User owner = createRandomUser();

    int toInsert = 3;
    for (int i = 0; i < toInsert; i++) {
      assignmentRepository.save(buildAssignment(owner));
    }

    long total = assignmentRepository.countAll();
    List<Assignment> result = assignmentRepository.findAllPaging((int) total + 10, 10);

    assertThat(result).isEmpty();
  }

  @Test
  void searchByTitlePaging_and_countByTitle_shouldWork() {
    User owner = createRandomUser();

    String keyword = generateRandomText(6);

    int toInsert = 5;
    for (int i = 0; i < toInsert; i++) {
      Assignment a =
          Assignment.builder()
              .title(generateRandomText(5) + keyword + generateRandomText(5))
              .description(generateRandomText(30))
              .subjectName(generateRandomText(10))
              .ownerId(owner.getId())
              .startTime(Timestamp.valueOf(generateRandomLocalDateTime()))
              .endTime(Timestamp.valueOf(generateRandomLocalDateTime()))
              .build();
      assignmentRepository.save(a);
    }

    List<Assignment> found = assignmentRepository.searchByTitlePaging(0, 20, keyword);
    long counted = assignmentRepository.countByTitle(keyword);

    assertThat(found.size()).isEqualTo(counted);
    assertThat(found).allMatch(a -> a.getTitle().toLowerCase().contains(keyword.toLowerCase()));
  }

  @Test
  void searchByTitlePaging_shouldBeCaseInsensitive() {
    User owner = createRandomUser();

    String kw = generateRandomText(5);
    String title1 = generateRandomText(4) + kw.toUpperCase() + generateRandomText(4);
    String title2 = generateRandomText(4) + kw.toLowerCase() + generateRandomText(4);

    Assignment a1 = buildAssignment(owner);
    Assignment a2 = buildAssignment(owner);

    a1.setTitle(title1);
    a2.setTitle(title2);

    assignmentRepository.save(a1);
    assignmentRepository.save(a2);

    List<Assignment> result = assignmentRepository.searchByTitlePaging(0, 10, kw);

    assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    assertThat(result).allMatch(a -> a.getTitle().toLowerCase().contains(kw.toLowerCase()));
  }

  @Test
  void searchByTitlePaging_shouldRespectOffsetAndLimit_withoutOverlapBetweenPages() {
    User owner = createRandomUser();

    String kw = generateRandomText(6);

    int toInsert = 8;
    for (int i = 0; i < toInsert; i++) {
      Assignment a = buildAssignment(owner);
      a.setTitle(generateRandomText(5) + kw + generateRandomText(5));
      assignmentRepository.save(a);
    }

    int pageSize = 3;

    List<Assignment> page1 = assignmentRepository.searchByTitlePaging(0, pageSize, kw);
    List<Assignment> page2 = assignmentRepository.searchByTitlePaging(pageSize, pageSize, kw);

    assertThat(page1).hasSize(pageSize);
    assertThat(page2).hasSize(pageSize);

    Set<Long> p1Ids = page1.stream().map(Assignment::getId).collect(Collectors.toSet());
    Set<Long> p2Ids = page2.stream().map(Assignment::getId).collect(Collectors.toSet());

    assertThat(p1Ids).doesNotContainAnyElementsOf(p2Ids);
  }

  @Test
  void searchByTitlePaging_shouldReturnEmpty_whenKeywordNotFound() {
    String notFoundKeyword = generateRandomText(12) + generateRandomText(12);

    List<Assignment> result = assignmentRepository.searchByTitlePaging(0, 10, notFoundKeyword);
    long count = assignmentRepository.countByTitle(notFoundKeyword);

    assertThat(result).isEmpty();
    assertThat(count).isZero();
  }
}
