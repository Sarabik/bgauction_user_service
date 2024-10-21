package com.bgauction.userservice.exceptionHandler;

import com.bgauction.userservice.exception.InvalidIdException;
import com.bgauction.userservice.exception.NotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorsResponse> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Not found in database", ex.getMessage());
        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorsResponse.setErrors(errors);
        return new ResponseEntity<>(errorsResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorsResponse> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Authentication error", ex.getMessage());
        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);
        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorsResponse> handleInvalidIdException(InvalidIdException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Invalid Id", ex.getMessage());
        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);
        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorsResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage
                ));

        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);

        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorsResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Database error", ex.getRootCause().getLocalizedMessage());

        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);

        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorsResponse> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) rootCause);
        }

        Map<String, String> errors = new HashMap<>();
        errors.put("Transaction error", rootCause != null ? rootCause.getMessage() : ex.getMessage());

        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);

        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorsResponse.setErrors(errors);

        return new ResponseEntity<>(errorsResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsResponse> handleGlobalException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Unexpected error", ex.getMessage());
        ErrorsResponse errorsResponse = new ErrorsResponse();
        errorsResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorsResponse.setErrors(errors);
        return new ResponseEntity<>(errorsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
