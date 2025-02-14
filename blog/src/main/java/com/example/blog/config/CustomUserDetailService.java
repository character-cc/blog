package com.example.blog.config;

import com.example.blog.dto.UserSummary;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            log.error("User not found");
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác ");
        }
        CustomUserDetail customUserDetail = new CustomUserDetail(new UserSummary(user.getId(), user.getUsername() ,
                user.getPassword(), user.getName() , user.getEmail() , user.getProvider() , user.getAvatar()));
        return customUserDetail;
    }
}
