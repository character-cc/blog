package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User,Long> {


    User findUserById(Long Id);

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u from User u where u.Id = :Id")
    @EntityGraph(attributePaths = {"categories"})
    User findUserByIdHavingCategories(@Param("Id") Long Id);

    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUserByUserName(@Param("keyword") String keyword , Pageable pageable);

}
