package com.example.blog.exception;

import com.example.blog.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String,String> response = new HashMap<>();
        response.put("message", "Không tìm thấy tài nguyên được yêu cầu");
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        Map<String,String> response = new HashMap<>();
        response.put("message", "Không có quyền truy cập");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

}
