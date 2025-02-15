package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;

import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping(value = "/home")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(postService.getPostSummaryForCategories());
    }

}

