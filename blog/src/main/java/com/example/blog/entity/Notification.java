package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "liked_or_cmt_post_id", nullable = true)
    private Post likedOrCmtPost;

    @ManyToOne
    @JoinColumn(name = "liked_comment_id", nullable = true)
    private Comment likedComment;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
