package vn.edu.humg.olympic.api.service;

import vn.edu.humg.olympic.api.model.AuthenticatedUser;

public interface UserService {
  AuthenticatedUser getCurrentUser();
}
