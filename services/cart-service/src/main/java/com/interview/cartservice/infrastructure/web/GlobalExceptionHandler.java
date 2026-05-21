package com.interview.cartservice.infrastructure.web;

import com.interview.cartservice.application.exception.ConflictException;
import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.commonsecurity.AbstractGlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception) {
        return notFound("CART_NOT_FOUND", exception);
    }

    @ExceptionHandler({ConflictException.class, IllegalStateException.class})
    ResponseEntity<Map<String, Object>> handleConflict(RuntimeException exception) {
        return conflict("CART_CONFLICT", exception);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ResponseEntity<Map<String, Object>> handleBadRequest(Exception exception) {
        return badRequest(exception);
    }
}