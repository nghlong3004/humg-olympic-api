package vn.edu.humg.olympic.api.service.impl;

import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.edu.humg.olympic.api.constant.JwtConstant;
import vn.edu.humg.olympic.api.converter.UserConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.UserUpdateRequest;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.model.response.UserResponse;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public AuthenticatedUser getCurrentUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var jwt = (Jwt) authentication.getPrincipal();
    Long id = jwt.getClaim(JwtConstant.USER_ID);
    var username = authentication.getName();
    String role = jwt.getClaimAsString(JwtConstant.SCOPE);
    var authorities = List.of(new SimpleGrantedAuthority(role));
    log.info("Get current user -> id:{}, username:{}, role:{}", id, username, role);
    return AuthenticatedUser.builder().id(id).username(username).authorities(authorities).build();
  }

  @Override
  public UserResponse getUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var jwt = (Jwt) authentication.getPrincipal();
    Long id = jwt.getClaim(JwtConstant.USER_ID);
    return this.getUser(id);
  }

  @Override
  public UserResponse getUser(Long id) {
    log.debug("Get user by userId:{}", id);
    User user =
        userRepository.findById(id).orElseThrow(() -> new ResourceException(ErrorCode.NOT_FOUND));
    log.debug("Get user successfully by id:{}", id);
    return UserConverter.to(user);
  }

  @Override
  public void update(UserUpdateRequest request) {
    var authenticatedUser = this.getCurrentUser();
    log.debug("Update user by userId:{}", authenticatedUser.getId());
    if (!authenticatedUser.isOwner(request.id()) && !authenticatedUser.isAdmin()) {
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }

    User user = new User();
    user.setId(request.id());
    user.setIsActive(true);
    applyUpdate(user, request);

    log.debug(
        "Update user successfully for id{} -> id:{}", authenticatedUser.getId(), request.id());
    userRepository.update(user);
  }

  @Override
  public PageResponse<UserResponse> search(int page, int size, String keyword, Role role) {
    log.debug("Search user by keyword:{} and role:{}", keyword, role);

    int offset = page * size;
    keyword = "%" + keyword + "%";
    String roleString = "%" + role.name() + "%";

    int total = userRepository.countByKeywordAndRole(keyword, roleString);
    if (total == 0) {
      return buildPageResponse(List.of(), page, size, total);
    }

    var users = userRepository.search(offset, size, keyword, roleString);
    return buildPageResponse(UserConverter.to(users), page, size, total);
  }

  private PageResponse<UserResponse> buildPageResponse(
      List<UserResponse> items, int page, int size, long totalItems) {

    int totalPages = (int) Math.ceil((double) totalItems / size);

    return PageResponse.<UserResponse>builder()
        .items(items)
        .page(page)
        .size(size)
        .totalItem(totalItems)
        .totalPage(totalPages)
        .build();
  }

  private void applyUpdate(User user, UserUpdateRequest request) {
    if (StringUtils.isNotBlank(request.firstName())) {
      user.setFirstName(request.firstName());
    }
    if (StringUtils.isNotBlank(request.lastName())) {
      user.setLastName(request.lastName());
    }
    if (request.gender() != null) {
      user.setGender(request.gender());
    }
    if (request.role() != null) {
      user.setRole(request.role());
    }
    if (StringUtils.isNotBlank(request.phone())) {
      user.setPhone(request.phone());
    }
    if (request.isActive() != null) {
      user.setIsActive(request.isActive());
    }
    if (request.birthday() != null) {
      user.setBirthday(request.birthday());
    }
    if (StringUtils.isNotBlank(request.universityName())) {
      user.setUniversityName(request.universityName());
    }
    if (StringUtils.isNotBlank(request.facultyName())) {
      user.setFacultyName(request.facultyName());
    }
    if (StringUtils.isNotBlank(request.avatarUrl())) {
      user.setAvatarUrl(request.avatarUrl());
    }
  }
}
