package vn.edu.humg.olympic.api.service;

import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.response.UserResponse;

public interface UserService {
  AuthenticatedUser getCurrentUser();

  UserResponse getUser();

  UserResponse getUser(Long id);
}
