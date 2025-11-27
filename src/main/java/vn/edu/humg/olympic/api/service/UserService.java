package vn.edu.humg.olympic.api.service;

import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.UserUpdateRequest;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.model.response.UserResponse;

public interface UserService {
  AuthenticatedUser getCurrentUser();

  UserResponse getUser();

  UserResponse getUser(Long id);

  void update(UserUpdateRequest request);

  PageResponse<UserResponse> search(int page, int size, String keyword, Role role);
}
