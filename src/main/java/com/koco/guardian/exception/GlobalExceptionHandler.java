package com.koco.guardian.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.koco.guardian.enums.ApplicationCode;
import com.koco.guardian.response.ApplicationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Request: {} {} raised {}";
    private static final String ERROR = "Error: ";

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFormat(InvalidFormatException ex) {
        Class<?> targetType = ex.getTargetType();
        if (targetType.isEnum()) {
            Object invalidValue = ex.getValue();
            Object[] enumValues = targetType.getEnumConstants();
            String allowed = String.join(", ",
                    List.of(enumValues).stream().map(Object::toString).toList());
            String message = String.format(
                    "Invalid value '%s'. Allowed values for %s: %s",
                    invalidValue, targetType.getSimpleName(), allowed
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Invalid enum value",
                            "message", message
                    ));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", " Invalid request format",
                        "message", "Please try again"
                ));
    }

    @ExceptionHandler(KocopeException.class)
    public ResponseEntity<ApplicationResponse<String>> handleKocopeException(HttpServletRequest req,
            KocopeException ex) {
        log.error(LOG_FORMAT, req.getMethod(), req.getRequestURL(), ex.getErrorMessage());
        int errorCode = ex.getCode() != null ? ex.getCode() : ApplicationCode.FAILURE.getCode();
        ApplicationResponse<String> response = new ApplicationResponse<>(errorCode,
                ex.getErrorMessage(), ex.getErrorMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                             .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationResponse<String>> handleException(HttpServletRequest req, Exception ex) {
        log.error(LOG_FORMAT, req.getMethod(), req.getRequestURL(), ex.getMessage());
        log.error(ERROR, ex);
        ApplicationResponse<String> response = new ApplicationResponse<>(ApplicationCode.FAILURE.getCode(),
                ApplicationCode.FAILURE.getMessage(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApplicationResponse<String>> handleMethodArgumentNotValidException(HttpServletRequest req,
            MethodArgumentNotValidException ex) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error : ex.getBindingResult()
                                  .getFieldErrors()) {
            errorMessages.add(error.getField() + ' ' + error.getDefaultMessage());
        }

        String errorMessage = String.join(", ", errorMessages);
        log.error(LOG_FORMAT, req.getMethod(), req.getRequestURL(), errorMessage);
        ApplicationResponse<String> response = new ApplicationResponse<>(ApplicationCode.FAILURE.getCode(), errorMessage,
                errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }
}