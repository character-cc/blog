package com.example.blog.util;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
@Getter
@Setter
public class ApiResponse <T>{

    private boolean success;
    private String message;
    private T data;
    private UserDTO user;

    public ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomOidcUser) {
            this.user = ((CustomOidcUser) authentication.getPrincipal()).getUserDTO();
        }
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(data, message, true);
    }

    public static <T> ApiResponse<T> error(T data , String message) {
        return new ApiResponse<T>(data, message, false);
    }

}
