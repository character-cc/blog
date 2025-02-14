package com.example.blog.dto;

import com.example.blog.entity.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserSummary {

    private Long Id;


    private String userName;

    private  String password;

    private String name;

    private String email;

    private String provider;

    private String avatar ;

    public User toUser() {
        User user = new User();
        user.setId(Id);
        user.setPassword(password);
        user.setName(userName);
        user.setEmail(email);
        user.setProvider(provider);
        user.setAvatar(avatar);
        user.setName(name);
        return user;
    }
}
