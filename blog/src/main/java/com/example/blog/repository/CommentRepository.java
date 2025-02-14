package com.example.blog.repository;

import com.example.blog.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {

    int countCommentByPostId(Long postId);

    @EntityGraph(attributePaths = {"likes"})
    Optional<Comment> findWithLikesById(Long commentId);

    @EntityGraph(attributePaths = {"likes" , "user"})
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findWithUserAndLikesByPostId(@Param("postId") Long postId , Pageable pageable);

}
