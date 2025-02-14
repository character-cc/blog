package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private Set<String> categories ;

    public static UserDTO fromUser(User user) {
        Set<String> categories = new HashSet<>();
        user.getCategories().forEach(category -> {categories.add(category.getName());});
        return new UserDTO(user.getId(), user.getName(), user.getEmail(),categories);
    }
}
