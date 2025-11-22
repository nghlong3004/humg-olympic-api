package vn.edu.humg.olympic.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.converter.UserConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.model.response.LoginResponse;
import vn.edu.humg.olympic.api.repository.UserRepository;
import vn.edu.humg.olympic.api.service.AuthService;
import vn.edu.humg.olympic.api.service.TokenService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Value("${application.security.jwt.refresh-expiration}")
    private int refreshExpirationMinutes;
    @Value("${application.security.jwt.refresh-name}")
    private String name;
    @Value("${application.security.cookie.secure}")
    private boolean cookieSecure;
    @Value("${application.security.cookie.same-site}")
    private String sameSite;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String normalizedEmail = request.email()
                                        .toLowerCase();
        userRepository.findByEmail(normalizedEmail)
                      .ifPresent(e -> {
                          throw new ResourceException(ErrorCode.EMAIL_ALREADY);
                      });

        User user = userConverter.from(request);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new ResourceException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = tokenService.generateRefreshToken(authentication);

        ResponseCookie refreshCookie = ResponseCookie.from(name, refreshToken)
                                                     .httpOnly(true)
                                                     .secure(cookieSecure)
                                                     .path(APIConstant.API_AUTH_PATH)
                                                     .maxAge(refreshExpirationMinutes * 60L)
                                                     .sameSite(sameSite)
                                                     .build();

        return new LoginResponse(refreshCookie, new AuthResponse(accessToken));
    }

    @Override
    public ResponseCookie logout() {

        return ResponseCookie.from(name, "")
                             .httpOnly(true)
                             .secure(cookieSecure)
                             .path(APIConstant.API_AUTH_PATH)
                             .maxAge(0)
                             .sameSite(sameSite)
                             .build();
    }

}
