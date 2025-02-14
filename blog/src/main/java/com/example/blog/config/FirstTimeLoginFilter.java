package com.example.blog.config;

import com.example.blog.repository.UserRepository;
import com.example.blog.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FirstTimeLoginFilter extends OncePerRequestFilter {


    private UserRepository userRepository;


    private UserContext userContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String path = request.getRequestURI();
        if (path.contains("/categories") || path.contains("/upload-categories") || path.contains("/categories/") || path.contains("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.contains("/home") && auth.getPrincipal() instanceof UserSecurity &&
                userRepository.findWithCategoriesById(userContext.getUserId()).getCategories().isEmpty()) {
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("error", "Người dùng bắt buộc phải có danh mục yêu thích");
            jsonResponse.put("statusCode" , 307);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(jsonResponse);
            response.setStatus(307);
            response.getWriter().write(json);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
