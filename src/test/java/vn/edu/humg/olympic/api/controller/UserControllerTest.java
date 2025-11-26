package vn.edu.humg.olympic.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.sql.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.filter.JwtAuthenticationFilter;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.model.request.UserUpdateRequest;
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
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value(user.getEmail()));

    Mockito.verify(userService).getUser();
  }

  @Test
  void getUser_shouldReturnUserById() throws Exception {
    User user = generateUser();
    Long id = user.getId();
    Mockito.when(userService.getUser(id)).thenReturn(UserConverter.to(user));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(APIConstant.API_USER_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.email").value(user.getEmail()));

    Mockito.verify(userService).getUser(id);
  }

  @Test
  void update_shouldCallServiceAndReturnOk() throws Exception {
    long id = (long) GenerateRandom.generateNumber(1_000_000);
    String firstName = GenerateRandom.generateRandomText(10);
    String lastName = GenerateRandom.generateRandomText(8);
    String phone = GenerateRandom.generateRandomVNPhoneNumber();
    String universityName = GenerateRandom.generateRandomText(15);
    String facultyName = GenerateRandom.generateRandomText(15);
    String avatarUrl = GenerateRandom.generateRandomText(20);

    String body =
        """
            {
              "id": %d,
              "firstName": "%s",
              "lastName": "%s",
              "phone": "%s",
              "universityName": "%s",
              "facultyName": "%s",
              "avatarUrl": "%s"
            }
            """
            .formatted(id, firstName, lastName, phone, universityName, facultyName, avatarUrl);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(APIConstant.API_USER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(MockMvcResultMatchers.status().isOk());

    ArgumentCaptor<UserUpdateRequest> captor = ArgumentCaptor.forClass(UserUpdateRequest.class);
    Mockito.verify(userService).update(captor.capture());

    UserUpdateRequest request = captor.getValue();
    Assertions.assertEquals(id, request.id());
    Assertions.assertEquals(firstName, request.firstName());
    Assertions.assertEquals(lastName, request.lastName());
    Assertions.assertEquals(phone, request.phone());
    Assertions.assertEquals(universityName, request.universityName());
    Assertions.assertEquals(facultyName, request.facultyName());
    Assertions.assertEquals(avatarUrl, request.avatarUrl());
  }

  @Test
  void getUser_shouldReturnNotFound_whenServiceThrowNotFound() throws Exception {
    long id = (long) GenerateRandom.generateNumber(1_000_000);

    Mockito.when(userService.getUser(id)).thenThrow(new ResourceException(ErrorCode.NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(APIConstant.API_USER_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());

    Mockito.verify(userService).getUser(id);
  }

  @Test
  void update_shouldReturnForbidden_whenServiceThrowForbidden() throws Exception {
    long id = (long) GenerateRandom.generateNumber(1_000_000);
    String firstName = GenerateRandom.generateRandomText(10);
    String lastName = GenerateRandom.generateRandomText(10);

    String body =
        """
            {
              "id": %d,
              "firstName": "%s",
              "lastName": "%s"
            }
            """
            .formatted(id, firstName, lastName);

    Mockito.doThrow(new ResourceException(ErrorCode.FORBIDDEN))
        .when(userService)
        .update(Mockito.any(UserUpdateRequest.class));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(APIConstant.API_USER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(MockMvcResultMatchers.status().isForbidden());

    Mockito.verify(userService).update(Mockito.any(UserUpdateRequest.class));
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
