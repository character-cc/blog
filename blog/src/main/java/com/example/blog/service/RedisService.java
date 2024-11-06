package com.example.blog.service;

import com.example.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String POST_VOTE_KEY_PREFIX = "post:votes:"; // For post votes

    private final String USER_VOTE_KEY_PREFIX = "user:votes:"; // For user votes

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Cacheable(cacheNames = "userss")
    public User getData(){
        return new User("shd","hds","jjsd");
    }

    private final String POST_VOTE_COUNT_KEY = "post:voteCounts";

    public String voteForPost(Long postId, Long userId) {
        String postVoteKey = "post:votes:" + postId;
        String userVoteKey = "user:votes:" + userId;

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

    public Set<Object> getTopVotedPostIds(int topN) {
        return redisTemplate.opsForZSet()
                .reverseRange(POST_VOTE_COUNT_KEY, 0, topN - 1);
    }

    // Lấy số lượng vote của một bài viết cụ thể
    public Double getVoteCount(Long postId) {
        return redisTemplate.opsForZSet().score(POST_VOTE_COUNT_KEY, postId);
    }

    public void clearVotes() {
        Set<String> postKeys = redisTemplate.keys(POST_VOTE_KEY_PREFIX + "*");
        if (postKeys != null && !postKeys.isEmpty()) {
            redisTemplate.delete(postKeys);
        }

        Set<String> userKeys = redisTemplate.keys(USER_VOTE_KEY_PREFIX + "*");
        if (userKeys != null && !userKeys.isEmpty()) {
            redisTemplate.delete(userKeys);
        }

        redisTemplate.delete(POST_VOTE_COUNT_KEY);

    }

    public Set<Object> getVotersForPost(Long postId) {
        String key = POST_VOTE_KEY_PREFIX + postId;
        return redisTemplate.opsForSet().members(key);
    }

    public Set<Object> getVotesByUserIds(Long userId) {
        String key = USER_VOTE_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().members(key);
    }

    public Long getVoteCountForPost(Long postId) {
        String key = POST_VOTE_KEY_PREFIX + postId;
        return redisTemplate.opsForSet().size(key);
    }
}
