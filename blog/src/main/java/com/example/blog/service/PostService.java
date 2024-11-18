package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void addPost(Post post){
        postRepository.save(post);
    }

    public List<Post> getPostsByIds(List<Long> postIds) {
        return postRepository.findByIdIn(postIds);
    }

    public List<Post> getAllPostById(Long id){
        return postRepository.findAllPostByUserId(id);
    }

    public void deletePostById(Long id){
        postRepository.deleteById(id);
    }

    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElse(null);
    }

    public void updatePost(Post post){
        postRepository.save(post);
    }
}

