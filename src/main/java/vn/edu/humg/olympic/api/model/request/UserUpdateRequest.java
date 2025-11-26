package vn.edu.humg.olympic.api.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import vn.edu.humg.olympic.api.model.Gender;
import vn.edu.humg.olympic.api.model.Role;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 11/26/2025
 */
public record UserUpdateRequest(
    @Min(1) Long id,
    @Size(max = 25) String firstName,
    @Size(max = 25) String lastName,
    Gender gender,
    @Size(min = 10, max = 12) String phone,
    Role role,
    Boolean isActive,
    Date birthday,
    @Size(max = 40) String universityName,
    @Size(max = 40) String facultyName,
    String avatarUrl) {}
