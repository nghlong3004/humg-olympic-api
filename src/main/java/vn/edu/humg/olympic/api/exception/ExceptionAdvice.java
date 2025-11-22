package vn.edu.humg.olympic.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ErrorResponse> handleResourceException(final ResourceException e) {
        final var errorCode = e.getErrorCode();
        final var errorResponse = errorCode.toErrorResponse();
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoElement(final NoSuchElementException e) {
        final var errorResponse = new ErrorResponse(e.getMessage(), 404, "not_found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException e
    ) {
        final var code = "ContentTypeIsNotSupported";
        final var errorResponse = new ErrorResponse(e.getMessage(), 400, code);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            final MissingServletRequestParameterException e
    ) {
        final var errorResponse = new ErrorResponse(e.getMessage(), 400, e.getParameterName().concat("_required"));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(
            final MissingRequestCookieException e
    ) {
        final var errorResponse = new ErrorResponse("Refresh token is missing.", 400, "MissingRefreshToken");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) {
        try {
            String missingField = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
            var errorResponse = new ErrorResponse("The required parameter is missing", 400,
                                                  missingField.concat("_required"));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException nullPointerException) {
            var errorResponse = new ErrorResponse("The required parameter is missing", 400, "parameter_required");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(final Exception e) {
        log.error(e.getMessage(), e);
        var errorResponse = ErrorCode.INTERNAL_ERROR.toErrorResponse();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
