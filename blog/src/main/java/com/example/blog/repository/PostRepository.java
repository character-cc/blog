package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"likePost","comments","categories"})
    List<Post> findPostByCreatedAtAfter(LocalDateTime postDateAfter);
}
