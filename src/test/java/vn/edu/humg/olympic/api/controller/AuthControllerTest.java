package vn.edu.humg.olympic.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.model.Gender;
import vn.edu.humg.olympic.api.model.request.LoginRequest;
import vn.edu.humg.olympic.api.model.request.RegisterRequest;
import vn.edu.humg.olympic.api.model.response.AuthResponse;
import vn.edu.humg.olympic.api.model.response.LoginResponse;
import vn.edu.humg.olympic.api.service.AuthService;

import java.sql.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnCreatedAndDelegateToService() throws Exception {
        RegisterRequest request = new RegisterRequest("Long", "Nguyen", "long@example.com", "123456", Gender.MALE,
                                                      Date.valueOf("1900-03-30"), null, null, null);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(APIConstant.API_AUTH_PATH + APIConstant.REGISTER).contentType(MediaType.APPLICATION_JSON)
                                                                              .content(json))
               .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnAuthResponseAndSetRefreshCookie() throws Exception {
        LoginRequest request = new LoginRequest("long@example.com", "123456");
        String json = objectMapper.writeValueAsString(request);

        ResponseCookie refreshCookie = ResponseCookie.from(APIConstant.REFRESH_TOKEN_NAME,
                                                           "asfsafwaruiwur192412u412irnfsakfbiwahfawf")
                                                     .httpOnly(true)
                                                     .path(APIConstant.API_AUTH_PATH)
                                                     .build();

        AuthResponse authResponse = new AuthResponse("asfsafwaruiwur192412u412irnfsakfbiwahfawf");
        LoginResponse loginResponse = new LoginResponse(refreshCookie, authResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post(APIConstant.API_AUTH_PATH + APIConstant.LOGIN).contentType(MediaType.APPLICATION_JSON)
                                                                           .content(json))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.SET_COOKIE, refreshCookie.toString()))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.accessToken").value("asfsafwaruiwur192412u412irnfsakfbiwahfawf"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void logout_shouldReturnNoContentAndSetExpiredCookie() throws Exception {
        ResponseCookie cookie = ResponseCookie.from(APIConstant.REFRESH_TOKEN_NAME, "")
                                              .maxAge(0)
                                              .path(APIConstant.API_AUTH_PATH)
                                              .httpOnly(true)
                                              .build();

        when(authService.logout()).thenReturn(cookie);

        mockMvc.perform(post(APIConstant.API_AUTH_PATH + APIConstant.LOGOUT)).andExpect(status().isNoContent());

        verify(authService).logout();
    }

    @Test
    void refreshToken_shouldUseCookieAndReturnNewAccessToken() throws Exception {
        String refreshToken = "askfjksdahfashfhasjfhsjafhsjadfasdf";

        AuthResponse response = new AuthResponse("asfsafwaruiwur192412u412irnfsakfbiwahfawf");
        when(authService.refreshToken(refreshToken)).thenReturn(response);

        mockMvc.perform(post(APIConstant.API_AUTH_PATH + APIConstant.REFRESH_TOKEN).cookie(
                       new Cookie(APIConstant.REFRESH_TOKEN_NAME, refreshToken)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.accessToken").value("asfsafwaruiwur192412u412irnfsakfbiwahfawf"));

        verify(authService).refreshToken(refreshToken);
    }
}
