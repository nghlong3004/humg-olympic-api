package vn.edu.humg.olympic.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.util.ReflectionTestUtils;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

  @Mock private JwtEncoder jwtEncoder;

  @Mock private JwtDecoder jwtDecoder;

  @InjectMocks private TokenServiceImpl tokenService;

  @Test
  void generateAccessToken_shouldReturnToken_whenAuthenticationValid() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String username = generateRandomEmail();
      String role = generateAuthority();
      String tokenValue = generateRandomText();

      Authentication authentication = mock(Authentication.class);
      when(authentication.getName()).thenReturn(username);
      when(authentication.getAuthorities())
          .thenReturn((Collection) AuthorityUtils.createAuthorityList(role));

      ReflectionTestUtils.setField(tokenService, "accessExpirationMinutes", 60);

      Jwt jwt = mock(Jwt.class);
      when(jwt.getTokenValue()).thenReturn(tokenValue);
      when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

      String token = tokenService.generateAccessToken(authentication);

      assertThat(token).isEqualTo(tokenValue);
      verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
      reset(jwtEncoder);
    }
  }

  @Test
  void generateRefreshToken_shouldReturnToken_whenAuthenticationValid() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String username = generateRandomEmail();
      String role = generateAuthority();
      String tokenValue = generateRandomText();

      Authentication authentication = mock(Authentication.class);
      when(authentication.getName()).thenReturn(username);
      when(authentication.getAuthorities())
          .thenReturn((Collection) AuthorityUtils.createAuthorityList(role));

      ReflectionTestUtils.setField(tokenService, "refreshExpirationMinutes", 120);

      Jwt jwt = mock(Jwt.class);
      when(jwt.getTokenValue()).thenReturn(tokenValue);
      when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

      String token = tokenService.generateRefreshToken(authentication);

      assertThat(token).isEqualTo(tokenValue);
      verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
      reset(jwtEncoder);
    }
  }

  @Test
  void getUsernameFromToken_shouldReturnSubject() {
    int n = generateNumber(15);
    for (int i = 0; i < n; ++i) {
      String token = generateRandomText();
      String username = generateRandomEmail();

      Jwt jwt = mock(Jwt.class);
      when(jwt.getSubject()).thenReturn(username);
      when(jwtDecoder.decode(token)).thenReturn(jwt);

      String result = tokenService.getUsernameFromToken(token);

      assertThat(result).isEqualTo(username);
      verify(jwtDecoder).decode(token);
      reset(jwtDecoder);
    }
  }

  @Test
  void validateAccessToken_shouldThrowInvalidAccessToken_whenJwtException() {
    String token = generateRandomText();

    doThrow(new JwtException("malformed")).when(jwtDecoder).decode(token);

    assertThatThrownBy(() -> tokenService.validateAccessToken(token))
        .isInstanceOf(ResourceException.class)
        .hasMessage(ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
  }

  @Test
  void validateRefreshToken_shouldThrowInvalidRefreshToken_whenJwtException() {
    String token = generateRandomText();

    doThrow(new JwtException("malformed")).when(jwtDecoder).decode(token);

    assertThatThrownBy(() -> tokenService.validateRefreshToken(token))
        .isInstanceOf(ResourceException.class)
        .hasMessage(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
  }

  @Test
  void validateAccessToken_shouldPass_whenValidType() {
    String token = generateRandomText();

    Jwt jwt = mock(Jwt.class);
    when(jwtDecoder.decode(token)).thenReturn(jwt);
    when(jwt.getClaimAsString("type")).thenReturn(APIConstant.ACCESS_TOKEN_NAME);

    tokenService.validateAccessToken(token);

    verify(jwtDecoder).decode(token);
  }

  @Test
  void validateRefreshToken_shouldPass_whenValidType() {
    String token = generateRandomText();

    Jwt jwt = mock(Jwt.class);
    when(jwtDecoder.decode(token)).thenReturn(jwt);
    when(jwt.getClaimAsString("type")).thenReturn(APIConstant.REFRESH_TOKEN_NAME);

    tokenService.validateRefreshToken(token);

    verify(jwtDecoder).decode(token);
  }
}
