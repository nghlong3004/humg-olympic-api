package vn.edu.humg.olympic.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.filter.JwtAuthenticationFilter;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.service.AssignmentService;
import vn.edu.humg.olympic.api.service.TokenService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
    controllers = AssignmentController.class,
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          classes = JwtAuthenticationFilter.class)
    })
@AutoConfigureMockMvc(addFilters = false)
class AssignmentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AssignmentService assignmentService;

  @MockitoBean private TokenService tokenService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void create_shouldReturnCreatedAndDelegateToService() throws Exception {
    AssignmentRequest request =
        new AssignmentRequest(
            generateRandomText(20),
            generateRandomText(40),
            generateRandomText(10),
            generateRandomTimestamp(),
            generateRandomTimestamp());

    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post(APIConstant.API_ASSIGNMENT_PATH + APIConstant.ASSIGNMENT_CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated());

    verify(assignmentService).create(any(AssignmentRequest.class));
  }

  @Test
  void list_shouldReturnPageResponseAndDelegateToService() throws Exception {
    int page = 0;
    int size = generateNumber(30);

    AssignmentResponse assignment =
        new AssignmentResponse(
            1L,
            generateRandomText(20),
            generateRandomText(40),
            generateRandomText(10),
            10L,
            generateRandomTimestamp(),
            generateRandomTimestamp(),
            true,
            generateRandomTimestamp(),
            generateRandomTimestamp());

    PageResponse<AssignmentResponse> response =
        PageResponse.<AssignmentResponse>builder()
            .items(List.of(assignment))
            .page(page)
            .size(size)
            .totalItems(1L)
            .totalPages(1)
            .build();

    when(assignmentService.list(page, size)).thenReturn(response);

    mockMvc
        .perform(
            get(APIConstant.API_ASSIGNMENT_PATH + APIConstant.ASSIGNMENT_LIST)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(page))
        .andExpect(jsonPath("$.size").value(size))
        .andExpect(jsonPath("$.totalItems").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.items[0].id").value(1L));

    verify(assignmentService).list(page, size);
  }

  @Test
  void list_shouldUseDefaultPagingWhenParamsMissing() throws Exception {
    int defaultPage = 0;
    int defaultSize = 20;

    PageResponse<AssignmentResponse> response =
        PageResponse.<AssignmentResponse>builder()
            .items(List.of())
            .page(defaultPage)
            .size(defaultSize)
            .totalItems(0L)
            .totalPages(0)
            .build();

    when(assignmentService.list(defaultPage, defaultSize)).thenReturn(response);

    mockMvc
        .perform(get(APIConstant.API_ASSIGNMENT_PATH + APIConstant.ASSIGNMENT_LIST))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(defaultPage))
        .andExpect(jsonPath("$.size").value(defaultSize))
        .andExpect(jsonPath("$.totalItems").value(0))
        .andExpect(jsonPath("$.totalPages").value(0))
        .andExpect(jsonPath("$.items").isArray());

    verify(assignmentService).list(defaultPage, defaultSize);
  }

  @Test
  void searchByTitle_shouldReturnPageResponseAndDelegateToService() throws Exception {
    int page = 0;
    int size = generateNumber(30);
    String keyword = generateRandomText(10);

    AssignmentResponse assignment =
        new AssignmentResponse(
            2L,
            generateRandomText(5) + keyword + generateRandomText(5),
            generateRandomText(40),
            generateRandomText(10),
            11L,
            generateRandomTimestamp(),
            generateRandomTimestamp(),
            true,
            generateRandomTimestamp(),
            generateRandomTimestamp());

    PageResponse<AssignmentResponse> response =
        PageResponse.<AssignmentResponse>builder()
            .items(List.of(assignment))
            .page(page)
            .size(size)
            .totalItems(1L)
            .totalPages(1)
            .build();

    when(assignmentService.searchByTitle(page, size, keyword)).thenReturn(response);

    mockMvc
        .perform(
            get(APIConstant.API_ASSIGNMENT_PATH + APIConstant.ASSIGNMENT_SEARCH)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(page))
        .andExpect(jsonPath("$.size").value(size))
        .andExpect(jsonPath("$.totalItems").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.items[0].id").value(2L));

    verify(assignmentService).searchByTitle(page, size, keyword);
  }

  @Test
  void searchByTitle_shouldUseDefaultPagingWhenOnlyKeywordProvided() throws Exception {
    int defaultPage = 0;
    int defaultSize = 20;
    String keyword = generateRandomText(12);

    PageResponse<AssignmentResponse> response =
        PageResponse.<AssignmentResponse>builder()
            .items(List.of())
            .page(defaultPage)
            .size(defaultSize)
            .totalItems(0L)
            .totalPages(0)
            .build();

    when(assignmentService.searchByTitle(defaultPage, defaultSize, keyword)).thenReturn(response);

    mockMvc
        .perform(
            get(APIConstant.API_ASSIGNMENT_PATH + APIConstant.ASSIGNMENT_SEARCH)
                .param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(defaultPage))
        .andExpect(jsonPath("$.size").value(defaultSize))
        .andExpect(jsonPath("$.totalItems").value(0))
        .andExpect(jsonPath("$.totalPages").value(0))
        .andExpect(jsonPath("$.items").isArray());

    verify(assignmentService).searchByTitle(defaultPage, defaultSize, keyword);
  }
}
