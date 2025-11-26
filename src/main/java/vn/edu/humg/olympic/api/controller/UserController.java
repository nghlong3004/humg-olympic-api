package vn.edu.humg.olympic.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.humg.olympic.api.model.response.UserResponse;
import vn.edu.humg.olympic.api.service.UserService;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 11/26/2025
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/me")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse me() {
    return userService.getUser();
  }

  @GetMapping(value = "/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUser(@PathVariable Long userId) {
    return userService.getUser(userId);
  }
}
