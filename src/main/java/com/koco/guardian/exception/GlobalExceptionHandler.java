package com.koco.guardian.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.koco.guardian.enums.ApplicationCode;
import com.koco.guardian.response.ApplicationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<ApplicationResponse<Map<String, String>>> handleMethodArgumentNotValidException(HttpServletRequest req,
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));
        ApplicationResponse<Map<String, String>> response =
                new ApplicationResponse<>(ApplicationCode.FAILURE.getCode(), "Bad " +
                        "request", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApplicationResponse<String>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        String errorMessage = "Invalid request body";
        // Check if it's an unknown property exception
        if (ex.getCause() instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) ex.getCause();
            errorMessage = "Unknown property: " + upe.getPropertyName();
        } else if (ex.getCause() instanceof InvalidFormatException) {
            errorMessage = "Invalid data format";
        } else if (ex.getCause() instanceof MismatchedInputException) {
            errorMessage = "Missing or invalid required field";
        }
        ApplicationResponse<String> response = new ApplicationResponse<>(ApplicationCode.FAILURE.getCode(), errorMessage,
                errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApplicationResponse<String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                                .stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
        ApplicationResponse<String> response = new ApplicationResponse<>(ApplicationCode.FAILURE.getCode(), errorMessage,
                errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
}