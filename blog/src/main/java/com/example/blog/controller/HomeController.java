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

    @GetMapping(value = "/home", produces = "application/json")
    public ResponseEntity<List<PostSummaryDTO>> home(Authentication authentication , HttpServletRequest request) {
//        ApiResponse<PostSummaryDTO> apiResponse = ApiResponse.success(postService.getPostsForYou(authentication , request),"jjd");
        return ResponseEntity.ok(postService.getPostsForYou(authentication , request));
    }

    @PostMapping("/posts/images/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file , HttpServletRequest request) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "//images/";

            System.out.println(uploadDir);
            File uploadFile = new File(uploadDir + fileName);
            uploadFile.getParentFile().mkdirs();
            file.transferTo(uploadFile);

            String imageUrl = "http://localhost/api/images/" + fileName;
//            ApiResponse<String> apiResponse = ApiResponse.success(imageUrl,"Yafnhha");

            Map<String, String> response = new HashMap<>();
            response.put("location", imageUrl);
            redisTemplate.opsForSet().add(KeyForRedis.getKeyForUploadImage(request.getSession().getId()), imageUrl);
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            ApiResponse<String> apiResponse = ApiResponse.success(e.getMessage(),"Yafnhha");
            Map<String, String> response = new HashMap<>();
            response.put("locatsfdsion", "jjd");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping(value = "/posts/upload")
    public ResponseEntity<?> createPost(@RequestBody PostRequestDTO postRequestDTO , Authentication authentication , HttpServletRequest request) {
//        System.out.println("Title: " + postRequest.getTitle());
//        System.out.println("Categories: " + postRequest.getCategories());
//        System.out.println("Content: " + postRequest.getContent());
        try {
            boolean success = postService.savePost(postRequestDTO, authentication, request);
            if (success) {
                return ResponseEntity.ok("Post created successfully!");
            }
            return ResponseEntity.badRequest().body("Post creation failed!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

