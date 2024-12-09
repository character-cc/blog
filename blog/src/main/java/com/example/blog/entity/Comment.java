package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id" ,nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "likes_comment",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likeComment = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(content, comment.content) && Objects.equals(user, comment.user) && Objects.equals(post, comment.post) && Objects.equals(createdAt, comment.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, createdAt);
    }
}
