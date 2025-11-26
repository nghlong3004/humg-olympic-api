package vn.edu.humg.olympic.api.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import java.sql.Date;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.model.response.LoginResponse;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private TokenService tokenService;

  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private AuthServiceImpl authService;

  @Test
  void register_shouldCreateNewUser_whenEmailNotExists() {

    var requests = randomRegisterRequests();
    for (var request : requests) {
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

      when(passwordEncoder.encode(request.password())).thenReturn(generateRandomText());
      authService.register(request);

      var userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());
      Mockito.reset(userRepository);
    }
  }

  @Test
  void register_shouldThrowBadRequest_whenEmailAlreadyExists() {
    var requests = randomRegisterRequests();
    for (var request : requests) {
      User existing = User.builder().id(1L).email(request.email()).build();

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existing));

      assertThatThrownBy(() -> authService.register(request))
          .isInstanceOf(ResourceException.class)
          .hasMessage(ErrorCode.EMAIL_ALREADY.getMessage());

      verify(userRepository, never()).save(any());
    }
  }

  private List<RegisterRequest> randomRegisterRequests() {
    var requests = new ArrayList<RegisterRequest>();
    int n = generateNumber(10);

    for (int i = 0; i < n; ++i) {
      var request =
          new RegisterRequest(
              generateRandomText(),
              generateRandomText(),
              generateRandomEmail(),
              generateRandomText(),
              generateGender(),
              Date.valueOf(generateRandomLocalDate()),
              generateRandomVNPhoneNumber(),
              generateRandomText(),
              generateRandomText());
      requests.add(request);
    }
    return requests;
  }

  @Test
  void login_shouldReturnTokensAndCookie_whenCredentialsValid() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String email = generateRandomEmail();
      String rawPassword = generateRandomText();
      String valueRefreshToken = generateRandomText();
      String valueAccessToken = generateRandomText();

      LoginRequest request = new LoginRequest(email, rawPassword);

      Authentication authentication = mock(Authentication.class);

      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);

      when(tokenService.generateAccessToken(authentication)).thenReturn(valueAccessToken);
      when(tokenService.generateRefreshToken(authentication)).thenReturn(valueRefreshToken);
      ReflectionTestUtils.setField(authService, "cookieSecure", true);
      ReflectionTestUtils.setField(authService, "refreshExpirationMinutes", 60);
      ReflectionTestUtils.setField(authService, "sameSite", "Strict");

      LoginResponse response = authService.login(request);

      AuthResponse authResponse = response.authResponse();
      assertThat(authResponse).isNotNull();
      assertThat(authResponse.accessToken()).isEqualTo(valueAccessToken);

      ResponseCookie refreshCookie = response.refreshCookie();
      assertThat(refreshCookie.getName()).isEqualTo(APIConstant.REFRESH_TOKEN_NAME);
      assertThat(refreshCookie.getValue()).isEqualTo(valueRefreshToken);
      assertThat(refreshCookie.isHttpOnly()).isTrue();
      assertThat(refreshCookie.isSecure()).isTrue();
      assertThat(refreshCookie.getPath()).isEqualTo(APIConstant.API_AUTH_PATH);
      assertThat(refreshCookie.getMaxAge()).isEqualTo(Duration.ofMinutes(60));
      assertThat(refreshCookie.getSameSite()).isEqualTo("Strict");

      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(tokenService).generateAccessToken(authentication);
      verify(tokenService).generateRefreshToken(authentication);
      Mockito.reset(authenticationManager);
      Mockito.reset(tokenService);
    }
  }

  @Test
  void login_shouldThrowInvalidCredentials_whenAuthenticationFails() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String email = generateRandomEmail();
      String rawPassword = generateRandomText();
      LoginRequest request = new LoginRequest(email, rawPassword);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(new BadCredentialsException("Bad credentials"));

      assertThatThrownBy(() -> authService.login(request))
          .isInstanceOf(ResourceException.class)
          .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());

      verify(tokenService, never()).generateAccessToken(any());
      verify(tokenService, never()).generateRefreshToken(any());
    }
  }

  @Test
  void logout_shouldReturnExpiredCookie() {
    ReflectionTestUtils.setField(authService, "cookieSecure", true);
    ReflectionTestUtils.setField(authService, "sameSite", "Strict");

    ResponseCookie cookie = authService.logout();

    assertThat(cookie.getName()).isEqualTo(APIConstant.REFRESH_TOKEN_NAME);
    assertThat(cookie.getValue()).isEmpty();
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isTrue();
    assertThat(cookie.getPath()).isEqualTo(APIConstant.API_AUTH_PATH);
    assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
    assertThat(cookie.getSameSite()).isEqualTo("Strict");
  }

  @Test
  void refreshToken_shouldReturnNewAccessToken_whenRefreshTokenValid() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String refreshToken = generateRandomText();
      String username = generateRandomEmail();
      String newAccessToken = generateRandomText();

      doNothing().when(tokenService).validateRefreshToken(refreshToken);

      when(tokenService.getUsernameFromToken(refreshToken)).thenReturn(username);

      var userDetails =
          org.springframework.security.core.userdetails.User.withUsername(username)
              .password("")
              .authorities(generateAuthority())
              .build();

      when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
      when(tokenService.generateAccessToken(any(Authentication.class))).thenReturn(newAccessToken);

      AuthResponse response = authService.refreshToken(refreshToken);

      assertThat(response).isNotNull();
      assertThat(response.accessToken()).isEqualTo(newAccessToken);

      verify(tokenService).validateRefreshToken(refreshToken);
      verify(tokenService).getUsernameFromToken(refreshToken);
      verify(userDetailsService).loadUserByUsername(username);
      verify(tokenService).generateAccessToken(any(Authentication.class));
      Mockito.reset(userDetailsService);
      Mockito.reset(tokenService);
    }
  }

  @Test
  void refreshToken_shouldThrowException_whenRefreshTokenInvalid() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String refreshToken = generateRandomText();

      doThrow(new ResourceException(ErrorCode.INVALID_REFRESH_TOKEN))
          .when(tokenService)
          .validateRefreshToken(refreshToken);

      assertThatThrownBy(() -> authService.refreshToken(refreshToken))
          .isInstanceOf(ResourceException.class)
          .hasMessage(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());

      verify(tokenService).validateRefreshToken(refreshToken);
      verify(tokenService, never()).getUsernameFromToken(anyString());
      verify(userDetailsService, never()).loadUserByUsername(anyString());
      verify(tokenService, never()).generateAccessToken(any());
    }
  }
}
