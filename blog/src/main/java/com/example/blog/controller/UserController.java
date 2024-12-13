package com.example.blog.controller;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.UnFollowUserDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Set;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
        if (authentication != null) {
            UserDTO userDTO = userService.getUserDTOById(((CustomOidcUser)authentication.getPrincipal()).getUserId());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping(value = "/unfollow_user")
    public ResponseEntity<?> unfollowUser(Authentication authentication , HttpServletRequest request) {
        try{
            Set<UnFollowUserDTO> unFollowUserDTOSet = userService.getUnFollowUser(authentication,request);
            return ResponseEntity.ok(unFollowUserDTOSet);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value = "/follow/{id}")
    public ResponseEntity<?> followUser(Authentication authentication , @PathVariable Long id) {
        try {
            userService.followUser(id,authentication);
            return ResponseEntity.ok(null);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

}
