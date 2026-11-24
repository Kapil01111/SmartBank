package com.smartbank.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("timestamp", LocalDateTime.now(), "status", 400, "errors", errors));
    }
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> badRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("timestamp", LocalDateTime.now(), "status", 400, "message", ex.getMessage()));
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFound(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("timestamp", LocalDateTime.now(), "status", 404, "message", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> other(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("timestamp", LocalDateTime.now(), "status", 500, "message", "An unexpected server error occurred"));
    }
}
