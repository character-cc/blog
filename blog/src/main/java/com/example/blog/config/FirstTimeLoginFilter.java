package com.example.blog.config;

import com.example.blog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class FirstTimeLoginFilter extends OncePerRequestFilter {


    private UserRepository userRepository;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
          String frontEndUrl = request.getHeader("Frontend-URL");
           Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("First time login filter");
        String path = request.getRequestURI();
        if(path.contains("/categories") || path.contains("/upload-categories")){
            filterChain.doFilter(request,response);
            return;
        }
          if(auth.getPrincipal() instanceof CustomOidcUser) {
              System.out.println("Qua lớp nàu đã đăng nhập");
              if(userRepository.findUserByIdHavingCategories((((CustomOidcUser) auth.getPrincipal()).getUserId())).getCategories().isEmpty()){
                  redisTemplate.opsForValue().set("redirectForCategories:" + ((CustomOidcUser) auth.getPrincipal()).getUserId() , true);
                  Map<String, String> jsonResponse = new HashMap<>();
                  jsonResponse.put("redirectFrontEndUrl", frontEndUrl);
                  ObjectMapper objectMapper = new ObjectMapper();
                  String json = objectMapper.writeValueAsString(jsonResponse);
                  System.out.println("có v đây");
                  response.setStatus(307);
                  response.getWriter().write(json);
                  return;
              }
              else {
                  if(redisTemplate.hasKey("redirectForCategories:" + ((CustomOidcUser) auth.getPrincipal()).getUserId())) {
                      redisTemplate.delete(redisTemplate.keys("redirectForCategories:" + ((CustomOidcUser) auth.getPrincipal()).getUserId()));

                  }
              }
          }
          filterChain.doFilter(request, response);
    }
}
