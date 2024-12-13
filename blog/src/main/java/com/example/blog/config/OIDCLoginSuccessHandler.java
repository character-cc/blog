package com.example.blog.config;

import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@AllArgsConstructor
public class OIDCLoginSuccessHandler implements AuthenticationSuccessHandler {

    private RedisTemplate<String, Object> redisTemplate;


    private UserService userService;

    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AuthenticationToken){
//            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//            OAuth2User principal = oauthToken.getPrincipal();
//            Map<String, Object> attributes = principal.getAttributes();
//            Collection<? extends GrantedAuthority> authorities = oauthToken.getAuthorities();
//            System.out.println("User Attributes:");
//            attributes.forEach((key, value) -> System.out.println(key + ": " + value));
//            System.out.println("\nUser Authorities:");
//            authorities.forEach(authority -> System.out.println(authority.getAuthority()));
//            System.out.println("Sau khi redirect" + request.getSession().getId());
            String frontendUrl = "http://localhost";
            String ipClient = request.getHeader("X-Forwarded-For");
            if (redisTemplate.opsForHash().hasKey("frontendUrl" , ipClient)) {
                frontendUrl = redisTemplate.opsForHash().get("frontendUrl" , ipClient).toString();
//                System.out.println("Thandh cong" + frontendUrl);
                redisTemplate.opsForHash().delete("frontendUrl" , ipClient );
            }
//            UserDTO userDTO = userService.getUserDTOById(((CustomOidcUser)authentication.getPrincipal()).getUserId());
//            Set<String> categories = userDTO.getCategories();
//            if(categories.isEmpty()) {
//                frontendUrl = frontendUrl + "?showCategoryModal=true";
//            }


            if(!redisTemplate.hasKey("redirectForCategories:" + ((CustomOidcUser) authentication.getPrincipal()).getUserId())){
                response.sendRedirect(frontendUrl);
                System.out.println("no redirectForCategories");
            }
        }
    }
}
