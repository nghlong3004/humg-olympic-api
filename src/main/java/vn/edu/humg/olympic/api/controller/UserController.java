package vn.edu.humg.olympic.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.UserUpdateRequest;
import vn.edu.humg.olympic.api.model.response.PageResponse;
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

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public void update(@Valid @RequestBody UserUpdateRequest request) {
    userService.update(request);
  }

  @GetMapping(value = "/search")
  @ResponseStatus(HttpStatus.OK)
  public PageResponse<UserResponse> search(
      @RequestParam @Size(max = 20, message = "full name must be at most 20 characters")
          String keyword,
      @RequestParam Role role,
      @RequestParam(defaultValue = "0") @Min(1) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return userService.search(page, size, keyword, role);
  }
}
