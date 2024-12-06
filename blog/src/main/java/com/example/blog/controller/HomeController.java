package com.example.blog.controller;

import com.example.blog.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @Autowired
    @Qualifier("POST_VOTE_KEY_PREFIX")
    private String POST_VOTE_KEY_PREFIX;

    @Autowired
    @Qualifier("USER_VOTE_KEY_PREFIX")
    private String USER_VOTE_KEY_PREFIX;

    @Autowired
    @Qualifier("POST_VOTE_COUNT_KEY")
    private String POST_VOTE_COUNT_KEY;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping(value = "/home", produces = "application/json")
    public ResponseEntity<ApiResponse<?>> home() {
        ApiResponse<String> apiResponse = ApiResponse.success("hhdhs","jjd");
        return ResponseEntity.ok(apiResponse);
    }
}
