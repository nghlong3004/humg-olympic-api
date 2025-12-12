package vn.edu.humg.olympic.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 12/12/2025
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
  // 400 Bad Request
  VALIDATION_ERROR(400, "VALIDATION_ERROR", "Validation failed for input data."),
  MISSING_PARAMETER(400, "MISSING_PARAMETER", "The required parameter is missing."),
  HTTP_MESSAGE_NOT_READABLE(400, "HTTP_MESSAGE_NOT_READABLE", "Malformed JSON request."),

  // 401 Unauthorized
  UNAUTHORIZED(401, "UNAUTHORIZED", "Full authentication is required."),
  TOKEN_EXPIRED(401, "TOKEN_EXPIRED", "Access token has expired."),

  // 403 Forbidden
  ACCESS_DENIED(403, "ACCESS_DENIED", "You do not have permission."),

  // 404 Not Found
  RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND", "Resources not found."),

  // 405 Method Not Allowed
  METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "Request method not supported."),

  // 409 Conflict
  DATA_CONFLICT(409, "DATA_CONFLICT", "Data conflict occurred."),

  // 413 Payload Too Large
  PAYLOAD_TOO_LARGE(413, "PAYLOAD_TOO_LARGE", "The payload is too large."),

  // 415 Unsupported Media Type
  UNSUPPORTED_MEDIA_TYPE(415, "UNSUPPORTED_MEDIA_TYPE", "Content type not supported."),

  // 429 Too Many Requests
  RATE_LIMIT_EXCEEDED(429, "RATE_LIMIT_EXCEEDED", "Too many requests."),

  // 500 Internal Server Error
  INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Unexpected server error."),

  // 503 Service Unavailable
  SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", "Service is unavailable.");

  private final int status;
  private final String code;
  private final String message;
}
