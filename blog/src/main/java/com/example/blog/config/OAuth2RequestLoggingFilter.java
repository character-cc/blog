package com.example.blog.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/oauth2/authorization/*")
@AllArgsConstructor
@Component
public class OAuth2RequestLoggingFilter extends OncePerRequestFilter {

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("s" + request.getSession().getId());
//        System.out.println(request.getRemoteAddr());
//            System.out.println("OAuth2 Request Parameters:");
//            Enumeration<String> parameterNames = request.getParameterNames();
//            while (parameterNames.hasMoreElements()) {
//                String paramName = parameterNames.nextElement();
//                String paramValue = request.getParameter(paramName);
//                System.out.println(paramName + " = " + paramValue);
//        }
         String frontEndUrl = request.getParameter("frontEndUrl");
            if (frontEndUrl != null) {
                String ipClient = request.getHeader("X-Forwarded-For");
                redisTemplate.opsForHash().put("frontendUrl", ipClient, frontEndUrl );
            }
        filterChain.doFilter(request, response);
    }
}
