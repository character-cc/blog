package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "author" , cascade = CascadeType.PERSIST)
    private List<Post> posts = new ArrayList<Post>();

    @OneToMany
    @JoinTable(
            name = "favorite_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> favoritePosts = new ArrayList<Post>();


    private List<Comment> comments = new ArrayList<Comment>();
}
