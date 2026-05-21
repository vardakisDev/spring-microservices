package com.interview.cartservice.infrastructure.web;

import com.interview.cartservice.api.model.ErrorResponse;
import com.interview.cartservice.application.exception.ConflictException;
import com.interview.cartservice.application.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("CART_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler({ConflictException.class, IllegalStateException.class})
    ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CART_CONFLICT", exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("BAD_REQUEST", exception.getMessage()));
    }

    private ErrorResponse error(String code, String message) {
        return new ErrorResponse().code(code).message(message).timestamp(OffsetDateTime.now());
    }
}