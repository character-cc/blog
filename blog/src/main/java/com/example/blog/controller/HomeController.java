package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import com.example.blog.service.PostService;
import com.example.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String USER_VOTE_KEY_PREFIX = "user:votes:"; // For user votes

    private final String POST_VOTE_COUNT_KEY = "post:voteCounts";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    PostService postService;

    @GetMapping("/home")
    public String home(Model model , Authentication authentication ) {
        int topN = 5;
        Set<Object> topPostIds = redisService.getTopVotedPostIds(topN);
        List<Long> postIds = topPostIds.stream()
                .filter(id -> id instanceof Long)
                .map(id -> (Long) id)
                .collect(Collectors.toList());

        // Truy vấn bài viết theo batch
        List<Post> topPosts = postService.getPostsByIds(postIds);
        Map<Long, Long> voteCounts = postIds.stream()
                .collect(Collectors.toMap(
                        postId -> postId,
                        postId -> redisService.getVoteCountForPost(postId)
                ));
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            userId = ((UserSecurity) authentication.getPrincipal()).getUser().getId(); // Giả định rằng UserSecurity chứa userId
        }

// Lưu giá trị userId vào một biến final
        final Long finalUserId = userId;

        Map<Long, Boolean> userVoteStatus = postIds.stream()
                .collect(Collectors.toMap(
                        postId -> postId,
                        postId -> redisTemplate.opsForSet().isMember(USER_VOTE_KEY_PREFIX + finalUserId, postId)
                ));

        model.addAttribute("topPosts", topPosts);
        model.addAttribute("voteCounts", voteCounts);
        model.addAttribute("userVoteStatus", userVoteStatus);
        model.addAttribute("userId", userId);
        return "home";
    }

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
            response.put("message", "User is not authenticated");
            return response;
        }

        try {
            String voteAction  = redisService.voteForPost(postId,userId);
            response.put("success", true);
            Long currentVotes = redisService.getVoteCountForPost(postId);
            System.out.println(currentVotes);
            response.put("currentVotes" , currentVotes);
            response.put("voteAction" ,voteAction);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing vote");
        }
        return response;
    }

    @PostMapping("/api/votes/toggle/{postId}")
    public ResponseEntity<String> toggleVote(@PathVariable Long postId) {
        // Get user information from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        Long userId = ((UserSecurity) authentication.getPrincipal()).getUser().getId(); // Assuming UserSecurity contains userId

        String postVoteKey = "post:" + postId + ":votes";
        String userVoteKey = "user:" + userId + ":votes";

        boolean hasVoted = redisTemplate.opsForSet().isMember(postVoteKey, userId);

        if (hasVoted) {
            // Remove vote
            redisTemplate.opsForSet().remove(postVoteKey, userId);
            redisTemplate.opsForSet().remove(userVoteKey, postId);
            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, -1);
            return ResponseEntity.ok("Vote removed!");
        } else {
            // Add vote
            redisTemplate.opsForSet().add(postVoteKey, userId);
            redisTemplate.opsForSet().add(userVoteKey, postId);
            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, 1);
            return ResponseEntity.ok("Vote added!");
        }
    }


}
