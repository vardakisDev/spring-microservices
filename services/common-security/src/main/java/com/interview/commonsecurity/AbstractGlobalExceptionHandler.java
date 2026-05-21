package com.interview.commonsecurity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractGlobalExceptionHandler {

    protected ResponseEntity<Map<String, Object>> notFound(String code, Exception exception) {
        return response(HttpStatus.NOT_FOUND, code, exception.getMessage());
    }

    protected ResponseEntity<Map<String, Object>> conflict(String code, Exception exception) {
        return response(HttpStatus.CONFLICT, code, exception.getMessage());
    }

    protected ResponseEntity<Map<String, Object>> badRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(errorBody(code, message));
    }

    private Map<String, Object> errorBody(String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("timestamp", OffsetDateTime.now());
        return body;
    }
}