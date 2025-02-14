package com.example.blog.util;

import com.example.blog.config.UserSecurity;
import com.example.blog.dto.UserSummary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
public class UserContext {

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        if (authentication.getPrincipal() instanceof UserSecurity) {
            UserSecurity userSecurity = (UserSecurity) authentication.getPrincipal();
            return userSecurity.getUserSummary().getId();
        }
        return null;
    }

    public UserSummary getUserSummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        if (authentication.getPrincipal() instanceof UserSecurity) {
            UserSecurity userSecurity = (UserSecurity) authentication.getPrincipal();
            return userSecurity.getUserSummary();
        }
        return null;
    }
}
