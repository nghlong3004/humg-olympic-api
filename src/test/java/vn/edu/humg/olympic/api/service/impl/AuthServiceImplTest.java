package vn.edu.humg.olympic.api.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.converter.UserConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Gender;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.model.response.LoginResponse;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.service.TokenService;

import java.sql.Date;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_shouldCreateNewUser_whenEmailNotExists() {
        var request = new RegisterRequest("Long", "Nguyen", "long@example.com", "123456", Gender.MALE,
                                          Date.valueOf("2004-03-30"), "0987123456", "HUMG", "Control Engineering");

        when(userRepository.findByEmail("long@example.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");
        when(userConverter.from(request)).thenReturn(User.builder()
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
                                                         .build());
        authService.register(request);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
    }

    @Test
    void register_shouldThrowBadRequest_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Long", "Nguyen", "long@example.com", "123456", Gender.MALE,
                                                      Date.valueOf("2004-03-30"), "0987123456", "HUMG",
                                                      "Control Engineering");

        User existing = User.builder()
                            .id(1L)
                            .email("long@example.com")
                            .build();

        when(userRepository.findByEmail("long@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(ResourceException.class)
                                                               .hasMessage(ErrorCode.EMAIL_ALREADY.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnTokensAndCookie_whenCredentialsValid() {
        String email = "long@example.com";
        String rawPassword = "123456";

        LoginRequest request = new LoginRequest(email, rawPassword);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(
                authentication);

        when(tokenService.generateAccessToken(authentication)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(authentication)).thenReturn("refresh-token");

        ReflectionTestUtils.setField(authService, "name", "refresh_token");
        ReflectionTestUtils.setField(authService, "cookieSecure", true);
        ReflectionTestUtils.setField(authService, "refreshExpirationMinutes", 60);
        ReflectionTestUtils.setField(authService, "sameSite", "Strict");

        LoginResponse response = authService.login(request);

        AuthResponse authResponse = response.authResponse();
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.accessToken()).isEqualTo("access-token");

        ResponseCookie refreshCookie = response.refreshCookie();
        assertThat(refreshCookie.getName()).isEqualTo("refresh_token");
        assertThat(refreshCookie.getValue()).isEqualTo("refresh-token");
        assertThat(refreshCookie.isHttpOnly()).isTrue();
        assertThat(refreshCookie.isSecure()).isTrue();
        assertThat(refreshCookie.getPath()).isEqualTo(APIConstant.API_AUTH_PATH);
        assertThat(refreshCookie.getMaxAge()).isEqualTo(Duration.ofMinutes(60));
        assertThat(refreshCookie.getSameSite()).isEqualTo("Strict");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateAccessToken(authentication);
        verify(tokenService).generateRefreshToken(authentication);
    }

    @Test
    void login_shouldThrowInvalidCredentials_whenAuthenticationFails() {
        String email = "long@example.com";
        String rawPassword = "wrong-password";

        LoginRequest request = new LoginRequest(email, rawPassword);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(
                new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(ResourceException.class)
                                                            .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());

        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void logout_shouldReturnExpiredCookie() {
        ReflectionTestUtils.setField(authService, "name", "refresh_token");
        ReflectionTestUtils.setField(authService, "cookieSecure", true);
        ReflectionTestUtils.setField(authService, "sameSite", "Strict");

        ResponseCookie cookie = authService.logout();

        assertThat(cookie.getName()).isEqualTo("refresh_token");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo(APIConstant.API_AUTH_PATH);
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO); // maxAge(0)
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
    }
}
