package com.compareit.app.global_handler;


import com.compareit.app.custom_exception.AuthenticationException;
import com.compareit.app.custom_exception.ResourceNotFoundException;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // add exception handling method - to handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse());
    }

    // add exception handling method - to handle auth exc

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse());
    }

    // catch all - handle ANY unchecked exception
    @ExceptionHandler(RuntimeException.class)
    //@ResponseStatus
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse());
    }

    // add exception handling method - to handleP.L validation failure - for req body (JSON payload)

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        System.out.println("in handle @Valid ");
        //1. get list of rejected fields
        List<FieldError> fieldErrors = e.getFieldErrors();
        //2. Covert it to Map <Key - field Name , Value - err mesg>
        Map<String, String> errorFieldMap = fieldErrors.stream() //Stream<FieldError>
                .collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));//f -> f.getField(), f -> f.getDefaultMessage()

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorFieldMap);
    }

}
