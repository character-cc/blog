package com.example.blog.controller;

import com.example.blog.dto.FollowUserDTO;
import com.example.blog.dto.PostSummaryDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import com.example.blog.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserContext userContext;

    @GetMapping(value = "/me")
    public ResponseEntity<?> getUser(Authentication authentication) {
        if(authentication != null) {
            UserDTO userDTO = userService.getUserDTOById(userContext.getUserId());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.badRequest().body(null);

    }

    @GetMapping(value = "/me/follows")
    public ResponseEntity<?> unfollowUser(@RequestParam(value = "page" , defaultValue = "0") int page, @RequestParam(value = "pageSize" , defaultValue = "5") int pageSize) {
        Set<FollowUserDTO> followUserDTOSet = userService.getFollow(page,pageSize);
        return ResponseEntity.ok(followUserDTOSet);
    }

    @PostMapping(value = "/me/follows/{userId}")
    public ResponseEntity<?> followUser(@PathVariable(value = "userId") Long userId) {
        userService.followUser(userId);
        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/me/story")
    public ResponseEntity<?> getStory(@RequestParam(value = "page" , defaultValue = "0") Integer page) {
        List<PostSummaryDTO> postSummaryDTOSet = postService.getMyStory(page);
        return ResponseEntity.ok(postSummaryDTOSet);
    }

}
