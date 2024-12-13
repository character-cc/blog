package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"likePost","comments","categories"})
    List<Post> findPostByCreatedAtAfter(LocalDateTime postDateAfter);

    @Query("SELECT p FROM Post p WHERE p.author.Id = :userId ORDER BY p.createdAt DESC")
    List<Post> findLatestPostByUser(@Param("userId") Long userId, Pageable pageable);
}
