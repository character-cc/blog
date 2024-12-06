package com.example.blog.config;

import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        User user = userRepository.findUserByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserName(name);
//            System.out.println(email);
            newUser.setProvider("keycloak");
            userRepository.save(newUser);
            return newUser;
        });
        return new CustomOidcUser(UserDTO.fromUsertoUserDTO(user),oidcUser);
    }

}
