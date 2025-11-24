package vn.edu.humg.olympic.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.edu.humg.olympic.api.constant.APIConstant;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.model.response.AssignmentResponse;
import vn.edu.humg.olympic.api.model.response.PageResponse;
import vn.edu.humg.olympic.api.service.AssignmentService;

@RestController
@RequestMapping(APIConstant.API_ASSIGNMENT_PATH)
@RequiredArgsConstructor
public class AssignmentController {

  private final AssignmentService assignmentService;

  @PostMapping(value = APIConstant.ASSIGNMENT_CREATE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@Valid @RequestBody AssignmentRequest request) {
    assignmentService.create(request);
  }

  @GetMapping(value = APIConstant.ASSIGNMENT_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<AssignmentResponse> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return assignmentService.list(page, size);
  }

  @GetMapping(value = APIConstant.ASSIGNMENT_SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<AssignmentResponse> searchByTitle(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam String keyword) {
    return assignmentService.searchByTitle(page, size, keyword);
  }
}
