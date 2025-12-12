package vn.edu.humg.olympic.api.exception;

import lombok.Getter;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 12/12/2025
 */
@Getter
public class ResourceException extends RuntimeException{
  private final ErrorCode errorCode;

  public ResourceException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
