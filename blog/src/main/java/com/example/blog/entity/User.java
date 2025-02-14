package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Users",  uniqueConstraints = {
        @UniqueConstraint(name = "UK_name", columnNames = "username"),
        @UniqueConstraint(name = "UK_email", columnNames = "email"),
}
)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String username;

    @Column(unique = true , nullable = false)
    private String email;

    private String name;

    private String provider;

    private String avatar = "http://localhost/api/images/avatar.png";

    private String password;

    @OneToMany(mappedBy = "author" , cascade = CascadeType.PERSIST)
    private Set<Post> posts = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "favorite_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> favoritePosts = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "categories_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private Set<Notification> notifications = new HashSet<>() {
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}
