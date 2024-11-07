package hhplus.ecommerce.application.common;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Entity not found error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.USER_NOT_FOUND);
        return ResponseEntity.status(ErrorCode.USER_NOT_FOUND.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.PRODUCT_NOT_FOUND);
        return ResponseEntity.status(ErrorCode.PRODUCT_NOT_FOUND.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.PAYMENT_FAILED);
        return ResponseEntity.status(ErrorCode.PAYMENT_FAILED.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorCode errorCode = ex.getMessage().equals(ErrorCode.DUPLICATE_REQUEST.getMessage())
                ? ErrorCode.DUPLICATE_REQUEST
                : ErrorCode.GENERIC_SERVER_ERROR;

        log.warn("Illegal state error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.info("Null pointer error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.GENERIC_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.GENERIC_SERVER_ERROR.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.GENERIC_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.GENERIC_SERVER_ERROR.getStatus()).body(errorResponse);
    }
}
