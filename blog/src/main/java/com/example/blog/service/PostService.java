package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByIds(List<Long> postIds) {
        return postRepository.findByIdIn(postIds);
    }
}

