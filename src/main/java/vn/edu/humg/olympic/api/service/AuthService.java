package vn.edu.humg.olympic.api.service;

import org.springframework.http.ResponseCookie;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.model.response.LoginResponse;

public interface AuthService {
    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ResponseCookie logout();

    AuthResponse refreshToken(String refreshToken);
}
