package com.example.blog.controller;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.FollowUserDTO;
import com.example.blog.dto.PostSummaryDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

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
            Set<FollowUserDTO> followUserDTOSet = userService.getUnFollowUser(authentication,request);
            return ResponseEntity.ok(followUserDTOSet);
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

    @GetMapping(value = "/me/story")
    public ResponseEntity<?> getStory(Authentication authentication , HttpServletRequest request) {
            Set<PostSummaryDTO> postSummaryDTOSet = postService.getMyStory(authentication,request);
            return ResponseEntity.ok(postSummaryDTOSet);
    }

}
