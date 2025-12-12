package vn.edu.humg.olympic.api.exception;

import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 12/12/2025
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = ResourceException.class)
  public ResponseEntity<ExceptionResponse> handlingResourceException(ResourceException exception) {
    return buildResponse(exception.getErrorCode());
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handlingMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    return buildResponse(ErrorCode.VALIDATION_ERROR);
  }

  @ExceptionHandler(value = MissingServletRequestParameterException.class)
  public ResponseEntity<ExceptionResponse> handlingMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    return buildResponse(ErrorCode.MISSING_PARAMETER);
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  public ResponseEntity<ExceptionResponse> handlingMessageNotReadableException(
      HttpMessageNotReadableException exception) {
    return buildResponse(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
  }

  @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ExceptionResponse> handlingHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException exception) {
    return buildResponse(ErrorCode.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<ExceptionResponse> handlingAccessDeniedException(
      AccessDeniedException exception) {
    return buildResponse(ErrorCode.ACCESS_DENIED);
  }

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  public ResponseEntity<ExceptionResponse> handlingMaxUploadSizeExceededException(
      MaxUploadSizeExceededException exception) {
    return buildResponse(ErrorCode.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(value = NoResourceFoundException.class)
  public ResponseEntity<ExceptionResponse> handlingNoResourceFound(
      NoResourceFoundException exception) {
    return buildResponse(ErrorCode.RESOURCE_NOT_FOUND);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ExceptionResponse> handlingRuntimeException(Exception exception) {
    log.error(exception.getMessage(), exception);

    return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ExceptionResponse> buildResponse(ErrorCode errorCode) {
    ExceptionResponse apiResponse =
        new ExceptionResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
  }
}
