package com.example.blog.config;

import com.example.blog.entity.UnAuthenticatedUser;
import com.example.blog.repository.UnAuthenticatedUserRepository;
import com.example.blog.service.RedisService;
import com.example.blog.util.UnAuthenticatedUserContext;
import com.example.blog.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class UnAuthenticatedUserFilter extends OncePerRequestFilter  {

    private UnAuthenticatedUserRepository unAuthenticatedUserRepository;

    private UnAuthenticatedUserContext unAuthenticatedUserContext;

    private UserContext userContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Long userId = userContext.getUserId();
        if(userId == null) {
            String identification = request.getHeader("identification");
            if(identification != null) {
                if(unAuthenticatedUserContext.getIdentification() == null) {
                    unAuthenticatedUserContext.setIdentification(identification);
                }

            }
            else {
                identification = UUID.randomUUID().toString();
                UnAuthenticatedUser unAuthenticatedUser = new UnAuthenticatedUser();
                unAuthenticatedUser.setId(identification);
                unAuthenticatedUserRepository.save(unAuthenticatedUser);
                response.setHeader("identification", identification);
                unAuthenticatedUserContext.setIdentification(identification);
            }
        }
        else {
            unAuthenticatedUserContext.setIdentification(null);
        }
        doFilter(request,response,filterChain);
    }
}
