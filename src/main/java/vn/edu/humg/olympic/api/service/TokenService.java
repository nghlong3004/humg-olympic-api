package vn.edu.humg.olympic.api.service;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    String getUsernameFromToken(String token);

    void validateAccessToken(String token);

    void validateRefreshToken(String token);

}
