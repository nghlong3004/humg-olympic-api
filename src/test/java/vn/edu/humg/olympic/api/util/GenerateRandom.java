package vn.edu.humg.olympic.api.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import vn.edu.humg.olympic.api.model.Gender;
import vn.edu.humg.olympic.api.model.Role;

public final class GenerateRandom {

  public static String generateRandomEmail() {
    return "%s@%s.com".formatted(generateRandomText(), generateRandomText(20));
  }

  public static String generateRandomText() {
    return generateRandomText(40);
  }

  public static int generateNumber(int max) {
    Random random = new Random();
    return random.nextInt(max) + 1;
  }

  public static String generateRandomText(int number) {
    Random random = new Random();
    String characters = "abcdefghijklmnopqrstuvwxyz";
    StringBuilder password = new StringBuilder();

    int usernameLength = random.nextInt(number) + 3;
    for (int i = 0; i < usernameLength; i++) {
      password.append(characters.charAt(random.nextInt(characters.length())));
    }

    return password.toString();
  }

  public static Gender generateGender() {
    Random random = new Random();
    var genders = Gender.values();
    return genders[random.nextInt(genders.length)];
  }

  public static String generateRole() {
    Random random = new Random();
    var roles = Role.values();
    return roles[random.nextInt(roles.length)].getAuthority();
  }

  public static String generateRandomVNPhoneNumber() {
    Random random = new Random();

    String[] prefixes = {"09", "08", "07", "03"};
    String prefix = prefixes[random.nextInt(prefixes.length)];

    StringBuilder remainingDigits = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      remainingDigits.append(random.nextInt(10));
    }
    return prefix + remainingDigits.toString();
  }

  public static LocalDate generateRandomLocalDate() {
    LocalDate startInclusive = LocalDate.of(1990, Month.JANUARY, 1);
    LocalDate endExclusive = LocalDate.of(2025, Month.DECEMBER, 31);
    long startEpochDay = startInclusive.toEpochDay();
    long endEpochDay = endExclusive.toEpochDay();
    long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
    return LocalDate.ofEpochDay(randomEpochDay);
  }

  public static LocalDateTime generateRandomLocalDateTime() {
    LocalDateTime startInclusive = LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0, 0);
    LocalDateTime endExclusive = LocalDateTime.of(2025, Month.DECEMBER, 31, 23, 59, 59);
    long startEpochSecond = startInclusive.toEpochSecond(java.time.ZoneOffset.UTC);
    long endEpochSecond = endExclusive.toEpochSecond(java.time.ZoneOffset.UTC);
    long randomEpochSecond = ThreadLocalRandom.current().nextLong(startEpochSecond, endEpochSecond);
    return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
  }

  public static Timestamp generateRandomTimestamp() {
    return Timestamp.valueOf(generateRandomLocalDateTime());
  }
}
