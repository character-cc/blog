package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.userName = :username")
    User findUserByUserName(String username);

    User findUserById(Long Id);
}
