package vn.edu.humg.olympic.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.edu.humg.olympic.api.constant.JwtConstant;
import vn.edu.humg.olympic.api.converter.AssignmentConverter;
import vn.edu.humg.olympic.api.exception.ErrorCode;
import vn.edu.humg.olympic.api.exception.ResourceException;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.request.AssignmentRequest;
import vn.edu.humg.olympic.api.repository.AssignmentRepository;
import vn.edu.humg.olympic.api.service.AssignmentService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

  private final AssignmentConverter assignmentConverter;
  private final AssignmentRepository assignmentRepository;

  @Override
  public void create(AssignmentRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var jwt = (Jwt) authentication.getPrincipal();

    String role = jwt.getClaimAsString(JwtConstant.SCOPE);

    if (Role.STUDENT.getAuthority().equals(role)) {
      throw new ResourceException(ErrorCode.FORBIDDEN);
    }

    Long ownerId = jwt.getClaim(JwtConstant.USER_ID);

    var username = authentication.getName();
    log.debug("username={} and role={} and ownerId={}", username, role, ownerId);

    var assignment = assignmentConverter.from(request);
    assignment.setOwnerId(ownerId);

    assignmentRepository.save(assignment);
    log.debug("Successfully save assignment");
  }
}
