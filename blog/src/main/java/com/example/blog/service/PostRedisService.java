package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostRedisService {
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

    public Set<Object> getTopVotedPostIds(int topN) {
        return redisTemplate.opsForZSet()
                .reverseRange(POST_VOTE_COUNT_KEY, 0, topN - 1);
    }


    public Map<Long, Long> getVoteCountsForPosts(List<Long> postIds) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long postId : postIds) {
                String key = POST_VOTE_KEY_PREFIX + postId;
                connection.sCard(key.getBytes());
            }
            return null;
        });

        Map<Long, Long> voteCounts = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            voteCounts.put(postIds.get(i), (Long) results.get(i));
        }
        return voteCounts;
    }

    public Map<Long, Boolean> getUserVoteStatus(List<Long> postIds , Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println(authentication);
            userId = ((UserSecurity) authentication.getPrincipal()).getUser().getId();

        }
        if (userId == null) {
            return Collections.emptyMap();
        }
        final Long finalUserId = userId;

        List<Object> userVoteStatuses = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long postId : postIds) {
                String key = USER_VOTE_KEY_PREFIX + finalUserId;
                connection.sIsMember(key.getBytes(),postId.toString().getBytes());
            }
            return null;
        });
        Map<Long, Boolean> userVoteStatus = new HashMap<>();
        for (int i = 0; i < postIds.size(); i++) {
            userVoteStatus.put(postIds.get(i), (Boolean) userVoteStatuses.get(i));
        }
        return userVoteStatus;
    }

    public String voteForPost(Long postId, Long userId) {
        String postVoteKey = POST_VOTE_KEY_PREFIX + postId;
        String userVoteKey = USER_VOTE_KEY_PREFIX + userId;
        if (!redisTemplate.opsForSet().isMember(postVoteKey, userId)) {
            redisTemplate.opsForSet().add(postVoteKey, userId);
            redisTemplate.opsForSet().add(userVoteKey, postId);
            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, 1);
            return "added";
        }
        else {
            redisTemplate.opsForSet().remove(postVoteKey, userId);
            redisTemplate.opsForSet().remove(userVoteKey, postId);
            redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY, postId, -1);
            return "removed";
        }
    }

    public Long getVoteCountForPost(Long postId) {
        String key = POST_VOTE_KEY_PREFIX + postId;
        return redisTemplate.opsForSet().size(key);
    }

    public void addPOST_VOTE_COUNT_KEY(Long postId, Long score){
        redisTemplate.opsForZSet().incrementScore(POST_VOTE_COUNT_KEY , postId ,score);
    }
}
