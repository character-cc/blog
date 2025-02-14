package com.example.blog.exception;

import com.example.blog.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
//        Map<String,String> response = new HashMap<>();
//        response.put("message", "Không tìm thấy tài nguyên được yêu cầu");
//        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
//    }


//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
//        Map<String,String> response = new HashMap<>();
//        response.put("message", "Không có quyền truy cập");
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value() ,
                "Tài khoản hoặc mật khẩu không chính xác"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException sqlEx = (ConstraintViolationException) ex.getCause();
            String constraintName = sqlEx.getConstraintName();
//            log.error("constraintName" + constraintName);
            if ("UK_name".equals(constraintName)) {
                return new ResponseEntity<>(
                        new ErrorResponse(HttpStatus.CONFLICT.value(), "Username đã tồn tại"),
                        HttpStatus.CONFLICT
                );
            }
            if ("UK_email".equals(constraintName)) {
                return new ResponseEntity<>(
                        new ErrorResponse(HttpStatus.CONFLICT.value(), "Email đã tồn tại"),
                        HttpStatus.CONFLICT
                );
            }

            Throwable rootCause = ex.getRootCause();

            if (rootCause instanceof SQLException) {
                String sqlMessage = rootCause.getMessage();
//                log.error("sqlMessage" + sqlMessage);
                if (sqlMessage.contains("FK_post_id")) {
                    return new ResponseEntity<>(
                            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bài viết không tồn tại hoặc đã bị xóa"),
                            HttpStatus.BAD_REQUEST
                    );
                }
                if (sqlMessage.contains("FK_user_id")) {
                    return new ResponseEntity<>(
                            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Người dùng không tồn tại"),
                            HttpStatus.BAD_REQUEST
                    );
                }
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Lỗi ràng buộc dữ liệu"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),errors), HttpStatus.BAD_REQUEST);
    }

}
