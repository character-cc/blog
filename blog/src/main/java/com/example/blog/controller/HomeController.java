package com.example.blog.controller;

import com.example.blog.dto.PostRequestDTO;
import com.example.blog.dto.PostSummaryDTO;
import com.example.blog.service.PostService;

import com.example.blog.util.KeyForRedis;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import java.util.UUID;

@RestController
public class HomeController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PostService postService;

    @GetMapping(value = "/home/{category}", produces = "application/json")
    public ResponseEntity<?> home(@PathVariable("category") String category,Authentication authentication , HttpServletRequest request) {
        try{
            return ResponseEntity.ok(postService.getPostsForCategory(authentication , request , category));
        }
        catch (Exception e){
            System.out.println("jasdhj" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

//        ApiResponse<PostSummaryDTO> apiResponse = ApiResponse.success(postService.getPostsForCategory(authentication , request),"jjd");

    }



}

