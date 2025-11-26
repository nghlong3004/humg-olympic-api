package vn.edu.humg.olympic.api.service.impl;

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
import vn.edu.humg.olympic.api.model.User;
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
    User user =
        userRepository.findById(id).orElseThrow(() -> new ResourceException(ErrorCode.NOT_FOUND));

    return UserConverter.to(user);
  }
}
