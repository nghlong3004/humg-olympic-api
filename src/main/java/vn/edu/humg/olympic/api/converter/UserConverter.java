package vn.edu.humg.olympic.api.converter;

import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.UserResponse;

public class UserConverter {
  public static User from(RegisterRequest request) {
    return User.builder()
        .firstName(request.firstName())
        .lastName(request.lastName())
        .email(request.email())
        .gender(request.gender())
        .birthday(request.birthday())
        .role(Role.STUDENT)
        .phone(request.phone())
        .universityName(request.universityName())
        .facultyName(request.facultyName())
        .avatarUrl(null)
        .isActive(true)
        .build();
  }

  public static UserResponse to(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .gender(user.getGender())
        .birthday(user.getBirthday())
        .role(user.getRole())
        .phone(user.getPhone())
        .universityName(user.getUniversityName())
        .facultyName(user.getFacultyName())
        .avatarUrl(user.getAvatarUrl())
        .created(user.getCreated())
        .build();
  }
}
