package vn.edu.humg.olympic.api.model.response;

import java.sql.Date;
import java.sql.Timestamp;
import lombok.Builder;
import vn.edu.humg.olympic.api.model.Gender;
import vn.edu.humg.olympic.api.model.Role;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 11/26/2025
 */
@Builder
public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Gender gender,
    Date birthday,
    Role role,
    String universityName,
    String facultyName,
    String phone,
    String avatarUrl,
    Timestamp created) {}
