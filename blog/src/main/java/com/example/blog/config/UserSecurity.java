package com.example.blog.config;

import com.example.blog.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class UserSecurity {
    UserSummary userSummary;
}
