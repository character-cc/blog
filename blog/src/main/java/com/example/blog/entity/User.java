package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "Users")
@Data
public class User {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String userName;

    @Column(unique = true)
    private String email;

    private String provider;

    private String avatar = "./api/images/avatar.png";

    @OneToMany(mappedBy = "author" , cascade = CascadeType.PERSIST)
    private Set<Post> posts = new HashSet<>();

    @OneToMany
    @JoinTable(
            name = "favorite_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> favoritePosts = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "categories_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private Set<Notification> notifications = new HashSet<>() {
    };

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id) && Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(provider, user.provider) && Objects.equals(avatar, user.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, userName, email, provider, avatar);
    }
}
