package hhplus.ecommerce.application.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("User error: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Input error: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.PAYMENT_FAILED;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        if (ErrorCode.DUPLICATE_REQUEST.getMessage().equals(ex.getMessage())) {
            log.warn("Duplicate request error: {}", ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.DUPLICATE_REQUEST);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        throw ex;
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.info("Null pointer error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("A null pointer error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.GENERIC_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.GENERIC_SERVER_ERROR.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}