package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByIds(List<Long> postIds) {
        return postRepository.findByIdIn(postIds);
    }

    public void addPost(Post post){
        postRepository.save(post);
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

