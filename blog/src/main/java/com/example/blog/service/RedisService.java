package com.example.blog.service;

import com.example.blog.util.KeyForRedis;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RedisService {

    private RedisTemplate<String, String> redisTemplate;

    public Set<Long> getTopPostIds(int start , int end) {
        String key = KeyForRedis.getKeyForPostScore();
        return Optional.ofNullable(redisTemplate.opsForZSet().reverseRange(key, start, end))
                .map(set -> set.stream().map(Long::valueOf).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    public Boolean isPostIdOfUser(Long postId ,Long userId) {
        return redisTemplate.opsForSet().isMember(KeyForRedis.getKeyForViewedPostToUser(userId), String.valueOf(postId));
    }

    public Boolean isPostIdOfUser(Long postId , String identification) {
        return redisTemplate.opsForSet().isMember(KeyForRedis.getKeyForViewedPostToUnAuthenticatedUser(identification), String.valueOf(postId) );
    }


    public Long getTotalPostView(Long postId){
        return redisTemplate.opsForSet().size(KeyForRedis.getKeyForPostView(postId));
    }

    //Comment
    public void setTotalPostComment(Long postId, int value ,long timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostComment(postId), String.valueOf(value), timeout, timeUnit);
    }

    public Integer getTotalPostComment(Long postId) {
        String value = redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostComment(postId));
        return (value != null) ? Integer.parseInt(value) : null;
    }

    public void incrementTotalPostComment(Long postId , int value){
        redisTemplate.opsForValue().increment(KeyForRedis.getKeyForTotalPostLike(postId), value);
    }


    //Like
    public void setTotalPostLike(Long postId, int value ,long timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(KeyForRedis.getKeyForTotalPostLike(postId), String.valueOf(value), timeout, timeUnit);
    }

    public void incrementTotalPostLike(Long postId , int value){
         redisTemplate.opsForValue().increment(KeyForRedis.getKeyForTotalPostLike(postId), value);
    }

    public Integer getTotalPostLike(Long postId) {
        String value = redisTemplate.opsForValue().get(KeyForRedis.getKeyForTotalPostLike(postId));
        return (value != null) ? Integer.parseInt(value) : null;
    }


    //
    public void addPostToCategory(String categoryName, Long postId , long timeout, TimeUnit timeUnit){
        redisTemplate.opsForSet().add(
                KeyForRedis.getKeyForCategory(categoryName), String.valueOf(postId));
    }

    public void  addPostScore(Long postId, long score){
        redisTemplate.opsForZSet().add(KeyForRedis.getKeyForPostScore(),String.valueOf(postId), score);
    }

    public void addUserIdToPostView(Long postId, Long userId){
        redisTemplate.opsForSet().add(KeyForRedis.getKeyForPostView(postId), String.valueOf(userId));
    }


    //
    public void addViewedPostIdToUser(Long postId, Long userId){
        redisTemplate.opsForSet().add(KeyForRedis.getKeyForViewedPostToUser(userId), String.valueOf(postId));
    }


    public void addViewedPostIdToUser(Long postId, String identification){
        redisTemplate.opsForSet().add(KeyForRedis.getKeyForViewedPostToUnAuthenticatedUser(identification), String.valueOf(postId));
    }

}

