package com.example.blog.service;

import com.example.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("POST_VOTE_KEY_PREFIX")
    private String POST_VOTE_KEY_PREFIX;

    @Autowired
    @Qualifier("USER_VOTE_KEY_PREFIX")
    private String USER_VOTE_KEY_PREFIX;

    @Autowired
    @Qualifier("POST_VOTE_COUNT_KEY")
    private String POST_VOTE_COUNT_KEY;

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



    public Set<Object> getTopVotedPostIds(int topN) {
        return redisTemplate.opsForZSet()
                .reverseRange(POST_VOTE_COUNT_KEY, 0, topN - 1);
    }

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

}
