package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import com.example.blog.service.PostRedisService;
import com.example.blog.service.PostService;
import com.example.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    RedisService redisService;

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

    @Autowired
    PostService postService;

    @Autowired
    private PostRedisService postRedisService;


    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        int topN = 5;
        Set<Object> topPostIds = postRedisService.getTopVotedPostIds(topN);
        List<Long> postIds = topPostIds.stream()
                .filter(id -> id instanceof Long)
                .map(id -> (Long) id)
                .collect(Collectors.toList());
        List<Post> topPosts = postService.getPostsByIds(postIds);
        Map<Long, Long> voteCounts = postRedisService.getVoteCountsForPosts(postIds);
        Map<Long, Boolean> userVoteStatus = postRedisService.getUserVoteStatus(postIds,authentication);
        model.addAttribute("topPosts", topPosts);
        model.addAttribute("voteCounts", voteCounts);
        model.addAttribute("userVoteStatus", userVoteStatus);
        return "home";
    }



//    @PostMapping("/api/votes/toggle/{postId}")
//    public ResponseEntity<String> toggleVote(@PathVariable Long postId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
//        }
//        Long userId = ((UserSecurity) authentication.getPrincipal()).getUser().getId(); // Assuming UserSecurity contains userId
//        String postVoteKey = "post:" + postId + ":votes";
//        String userVoteKey = "user:" + userId + ":votes";
//        boolean hasVoted = redisTemplate.opsForSet().isMember(postVoteKey, userId);
//
//        if (hasVoted) {
//            redisTemplate.opsForSet().remove(postVoteKey, userId);
//            redisTemplate.opsForSet().remove(userVoteKey, postId);
//            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, -1);
//            return ResponseEntity.ok("Vote removed!");
//        } else {
//
//            redisTemplate.opsForSet().add(postVoteKey, userId);
//            redisTemplate.opsForSet().add(userVoteKey, postId);
//            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, 1);
//            return ResponseEntity.ok("Vote added!");
//        }
//    }


}
