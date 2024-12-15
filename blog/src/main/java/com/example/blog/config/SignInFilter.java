package com.example.blog.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/oauth2/authorization/*")
@AllArgsConstructor
@Component
public class SignInFilter extends OncePerRequestFilter {

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("s" + request.getSession().getId());
//        System.out.println(request.getRemoteAddr());
//            Enumeration<String> parameterNames = request.getParameterNames();
//            while (parameterNames.hasMoreElements()) {
//                String paramName = parameterNames.nextElement();
//                String paramValue = request.getParameter(paramName);
//                System.out.println(paramName + " = " + paramValue);
//        }
        System.out.println(request.getSession().getId());
         String frontEndUrl = request.getParameter("frontEndUrl");
            if (frontEndUrl != null) {
                String ipClient = request.getHeader("X-Forwarded-For");
                redisTemplate.opsForHash().put("frontendUrl", ipClient, frontEndUrl );
            }
        filterChain.doFilter(request, response);
    }
}
