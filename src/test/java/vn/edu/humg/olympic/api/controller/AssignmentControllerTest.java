package vn.edu.humg.olympic.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.filter.JwtAuthenticationFilter;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.request.AssignmentUpdateRequest;
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
            post(APIConstant.API_ASSIGNMENT_PATH)
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
            .totalItem(1L)
            .totalPage(1)
            .build();

    when(assignmentService.list(page, size)).thenReturn(response);

    mockMvc
        .perform(
            get(APIConstant.API_ASSIGNMENT_PATH)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(page))
        .andExpect(jsonPath("$.size").value(size))
        .andExpect(jsonPath("$.totalItem").value(1))
        .andExpect(jsonPath("$.totalPage").value(1))
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
            .totalItem(0L)
            .totalPage(0)
            .build();

    when(assignmentService.list(defaultPage, defaultSize)).thenReturn(response);

    mockMvc
        .perform(get(APIConstant.API_ASSIGNMENT_PATH))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(defaultPage))
        .andExpect(jsonPath("$.size").value(defaultSize))
        .andExpect(jsonPath("$.totalItem").value(0))
        .andExpect(jsonPath("$.totalPage").value(0))
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
            .totalItem(1L)
            .totalPage(1)
            .build();

    when(assignmentService.search(page, size, keyword)).thenReturn(response);

    mockMvc
        .perform(
            get(APIConstant.API_ASSIGNMENT_PATH + "/search")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(page))
        .andExpect(jsonPath("$.size").value(size))
        .andExpect(jsonPath("$.totalItem").value(1))
        .andExpect(jsonPath("$.totalPage").value(1))
        .andExpect(jsonPath("$.items[0].id").value(2L));

    verify(assignmentService).search(page, size, keyword);
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
            .totalItem(0L)
            .totalPage(0)
            .build();

    when(assignmentService.search(defaultPage, defaultSize, keyword)).thenReturn(response);

    mockMvc
        .perform(get(APIConstant.API_ASSIGNMENT_PATH + "/search").param("keyword", keyword))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page").value(defaultPage))
        .andExpect(jsonPath("$.size").value(defaultSize))
        .andExpect(jsonPath("$.totalItem").value(0))
        .andExpect(jsonPath("$.totalPage").value(0))
        .andExpect(jsonPath("$.items").isArray());

    verify(assignmentService).search(defaultPage, defaultSize, keyword);
  }

  @Test
  void update_shouldReturnOkAndDelegateToService() throws Exception {
    Long id = (long) generateNumber(1_000_000);

    AssignmentUpdateRequest request =
        new AssignmentUpdateRequest(
            id,
            generateRandomText(20),
            generateRandomText(40),
            generateRandomText(10),
            generateRandomTimestamp(),
            generateRandomTimestamp(),
            true);

    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(APIConstant.API_ASSIGNMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    verify(assignmentService).update(any(AssignmentUpdateRequest.class));
  }

  @Test
  void delete_shouldReturnNoContentAndDelegateToService() throws Exception {
    Long id = (long) generateNumber(1_000_000);

    mockMvc
        .perform(
            delete(APIConstant.API_ASSIGNMENT_PATH + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(assignmentService).delete(id);
  }
}
