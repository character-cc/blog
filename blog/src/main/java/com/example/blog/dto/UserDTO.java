package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private Set<String> categories ;

    public static UserDTO fromUsertoUserDTO(User user) {
        Set<String> categories = new HashSet<>();
        user.getCategories().forEach(category -> {categories.add(category.getName());});
        return new UserDTO(user.getId(), user.getUserName(), user.getEmail(),categories);
    }
}
