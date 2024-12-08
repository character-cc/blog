package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByUserName(String username);

    User findUserById(Long Id);

    Optional<User> findUserByEmail(String email);

}
