package vn.edu.humg.olympic.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.service.AuthService;

@RestController
@RequestMapping(APIConstant.API_AUTH_PATH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = APIConstant.REGISTER, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {
        authService.register(request);
    }

    @PostMapping(value = APIConstant.LOGIN, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {
        var response = authService.login(request);

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, response.refreshCookie()
                                                                     .toString())
                             .body(response.authResponse());
    }

    @PostMapping(value = APIConstant.LOGOUT)
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                             .header(HttpHeaders.SET_COOKIE, authService.logout()
                                                                        .toString())
                             .build();
    }
}
