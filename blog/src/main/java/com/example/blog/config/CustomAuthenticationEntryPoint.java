package com.example.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.keycloak.models.ClientRegistrationAccessTokenConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ClientRegistrationRepository clientRegistrationRepository;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String frontendUrl = request.getHeader("Frontend-URL");
        String ipClient = request.getHeader("X-Forwarded-For");
//        System.out.println(frontendUrl);
//        System.out.println(ipClient);
        redisTemplate.opsForHash().put("frontendUrl", ipClient, frontendUrl );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
//        String redirectUrl = this.getAuthorizationUrl("keycloak" );
        String redirectUrl = "http://localhost/api//oauth2/authorization/keycloak";
//        request.getSession().setAttribute("feRedirectUrl", frontendUrl);
//        System.out.println("Trc khi redirect" + request.getSession().getId());
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("redirectUrl", redirectUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(jsonResponse);
        System.out.println("Witre Response");
        response.getWriter().write(json);
    }


    public String getAuthorizationUrl(String clientRegistrationId) {
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (clientRegistration != null) {
            String authorizationUri = clientRegistration.getProviderDetails().getAuthorizationUri();
            String clientId = clientRegistration.getClientId();
            String redirectUri = clientRegistration.getRedirectUri();
            String scopes = String.join(" ", clientRegistration.getScopes());
            System.out.println(authorizationUri);
            return authorizationUri;
        }
        return null;
    }
}
