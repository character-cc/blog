package com.example.blog.controller;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        if (authentication != null) {
            UserDTO userDTO = userService.getUserDTOById(((CustomOidcUser)authentication.getPrincipal()).getUserDTO().getId());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
