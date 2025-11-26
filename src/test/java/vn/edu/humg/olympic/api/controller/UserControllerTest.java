package vn.edu.humg.olympic.api.controller;

import java.sql.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.converter.UserConverter;
import vn.edu.humg.olympic.api.filter.JwtAuthenticationFilter;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.service.UserService;
import vn.edu.humg.olympic.api.util.GenerateRandom;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 11/26/2025
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          classes = JwtAuthenticationFilter.class)
    })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private UserService userService;

  @Test
  void me_shouldReturnUser() throws Exception {
    User user = generateUser();
    Mockito.when(userService.getUser()).thenReturn(UserConverter.to(user));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(APIConstant.API_USER_PATH + "/me")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());

    Mockito.verify(userService).getUser();
  }

  private User generateUser() {
    return User.builder()
        .id((long) GenerateRandom.generateNumber(1_000_000))
        .firstName(GenerateRandom.generateRandomText(20))
        .lastName(GenerateRandom.generateRandomText(20))
        .email(GenerateRandom.generateRandomEmail())
        .role(GenerateRandom.generateRole())
        .gender(GenerateRandom.generateGender())
        .phone(GenerateRandom.generateRandomVNPhoneNumber())
        .universityName(GenerateRandom.generateRandomText(20))
        .facultyName(GenerateRandom.generateRandomText(20))
        .birthday(Date.valueOf(GenerateRandom.generateRandomLocalDate()))
        .avatarUrl(GenerateRandom.generateRandomText())
        .created(GenerateRandom.generateRandomTimestamp())
        .updated(GenerateRandom.generateRandomTimestamp())
        .build();
  }
}
