package com.example.blog.controller;

import com.example.blog.dto.SignInRequestDTO;
import com.example.blog.dto.SignUpRequestDTO;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import com.example.blog.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  SecurityContextRepository securityContextRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@Valid @RequestBody SignInRequestDTO signInRequestDTO, HttpServletRequest request , HttpServletResponse response) {
        authUser(signInRequestDTO.getUsername(),signInRequestDTO.getPassword(), request, response);
        Map<String , Long> res = new HashMap<>();
        res.put("userId" , userContext.getUserId());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup( @Valid @RequestBody SignUpRequestDTO signUpRequestDTO, HttpServletRequest request , HttpServletResponse response) {
        User user = signUpRequestDTO.toUser();
        user.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        userService.saveUser(user);
        authUser(signUpRequestDTO.getUsername(),signUpRequestDTO.getPassword(), request, response);
        Map<String , Long> res = new HashMap<>();
        res.put("userId" , user.getId());
        return ResponseEntity.ok(res);
    }

    public void authUser(String username, String password , HttpServletRequest request , HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().invalidate();
        request.getSession(true);
        securityContextRepository.saveContext(new SecurityContextImpl(authentication), request, response);
    }
}
