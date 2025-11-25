package vn.edu.humg.olympic.api.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.constant.JwtConstant;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.AuthenticatedUser;
import vn.edu.humg.olympic.api.service.TokenService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
  @Value("${application.security.jwt.access-expiration}")
  private int accessExpirationMinutes;

  @Value("${application.security.jwt.refresh-expiration}")
  private int refreshExpirationMinutes;

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Override
  public String generateAccessToken(Authentication authentication) {
    return generateToken(authentication, accessExpirationMinutes, APIConstant.ACCESS_TOKEN_NAME);
  }

  @Override
  public String generateRefreshToken(Authentication authentication) {
    return generateToken(authentication, refreshExpirationMinutes, APIConstant.REFRESH_TOKEN_NAME);
  }

  @Override
  public String getUsernameFromToken(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
  }

  @Override
  public String getRole(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getClaimAsString(JwtConstant.SCOPE);
  }

  @Override
  public void validateAccessToken(String token) {
    validateTokenInternal(token, APIConstant.ACCESS_TOKEN_NAME);
  }

  @Override
  public void validateRefreshToken(String token) {
    validateTokenInternal(token, APIConstant.REFRESH_TOKEN_NAME);
  }

  private String generateToken(Authentication authentication, int expirationMinutes, String type) {
    Instant now = Instant.now();
    var principal = (AuthenticatedUser) authentication.getPrincipal();
    Long userId = principal.getId();
    String scope = principal.getAuthority();

    String username = authentication.getName();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(username)
            .issuedAt(now)
            .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
            .claim(JwtConstant.SCOPE, scope)
            .claim(JwtConstant.USER_ID, userId)
            .claim(JwtConstant.TYPE, type)
            .build();

    JwtEncoderParameters params =
        JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

    return jwtEncoder.encode(params).getTokenValue();
  }

  private void validateTokenInternal(String token, String expectedType) {
    try {
      Jwt jwt = jwtDecoder.decode(token);

      String type = jwt.getClaimAsString(JwtConstant.TYPE);
      if (!expectedType.equals(type)) {
        throw new ResourceException(getErrorCodeInvalid(expectedType));
      }

    } catch (JwtValidationException ex) {
      if (ex.getMessage().contains("expired")) {
        ErrorCode errorCode =
            expectedType.equals(APIConstant.ACCESS_TOKEN_NAME)
                ? ErrorCode.ACCESS_TOKEN_EXPIRED
                : ErrorCode.REFRESH_TOKEN_EXPIRED;
        throw new ResourceException(errorCode);
      }

      throw new ResourceException(getErrorCodeInvalid(expectedType));

    } catch (JwtException ex) {
      throw new ResourceException(getErrorCodeInvalid(expectedType));
    }
  }

  private ErrorCode getErrorCodeInvalid(String expectedType) {
    return expectedType.equals(APIConstant.ACCESS_TOKEN_NAME)
        ? ErrorCode.INVALID_ACCESS_TOKEN
        : ErrorCode.INVALID_REFRESH_TOKEN;
  }
}
