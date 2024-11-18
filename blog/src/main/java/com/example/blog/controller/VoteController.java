package com.example.blog.controller;

import com.example.blog.entity.UserSecurity;
import com.example.blog.service.PostRedisService;
import com.example.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class VoteController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private PostRedisService postRedisService;

    @PostMapping("/vote")
    @ResponseBody
    public Map<String, Object> vote(@RequestParam Long postId, Authentication authentication) {
        System.out.println("test");
        Map<String, Object> response = new HashMap<>();
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId = ((UserSecurity) authentication.getPrincipal()).getUser().getId();
        } else {
            response.put("success", false);
            response.put("message", "Cần đăng nhập");
            return response;
        }
        try {
            String voteAction  = postRedisService.voteForPost(postId,userId);
            response.put("success", true);
            Long currentVotes = postRedisService.getVoteCountForPost(postId);
            System.out.println(currentVotes);
            response.put("currentVotes" , currentVotes);
            response.put("voteAction" ,voteAction);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing vote");
        }
        return response;
    }
}
