package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {


    @EntityGraph(attributePaths = {"categories"})
    List<Post> findPostByCreatedAtAfter(LocalDateTime postDateAfter);

    @EntityGraph(attributePaths = {"author" , "images"})
    @Query("SELECT p FROM Post p WHERE p.author.Id = :userId ORDER BY p.updatedAt DESC")
    List<Post> findLatestPostByUser(@Param("userId") Long userId, Pageable pageable);


    @EntityGraph(attributePaths = {"author" , "images"})
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Post> searchPostsByTitle(@Param("title") String title , Pageable pageable);

    @EntityGraph(attributePaths = {"author" , "images"})
    List<Post> findWithAuthorAndImagesAllByIdIn(Collection<Long> postIds);

    @EntityGraph(attributePaths = {"images"})
    Optional<Post> findWithImagesById(Long postId);


    @EntityGraph(attributePaths = {"categories"})
    Optional<Post> findWithCategoriesById(Long postId);

    @EntityGraph(attributePaths = {"images" ,"author"})
    List<Post> findAllWithImagesAndAuthorByIdIn(Collection<Long> postIds);

    @EntityGraph(attributePaths = {"images" ,"author"})
    Optional<Post> findWithImagesAndAuthorById(Long postId);

    @Query("SELECT COUNT(u)  FROM Post p JOIN p.likes u WHERE p.id = :postId")
    int countLikesById(Long postId);

    @EntityGraph(attributePaths = "comment")
    Optional<Post> findWithCommentById(Long postId);

    @EntityGraph(attributePaths = {"likes"})
    Optional<Post> findWithLikesById(Long postId);

    @Query(value = "SELECT COUNT(*) FROM likes_post WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    long isLiked(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT p.id, COUNT(l) FROM Post p LEFT JOIN p.likes l WHERE p.id IN :postIds GROUP BY p.id")
    List<Object[]> getTotalLikesByPostIds(@Param("postIds") Set<Long> postIds);

    @Query("SELECT p.id, COUNT(c) FROM Post p LEFT JOIN p.comments c WHERE p.id IN :postIds GROUP BY p.id")
    List<Object[]> getTotalCommentsByPostIds(@Param("postIds") Set<Long> postIds);

}
