package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"categories"})
    User findWithCategoriesById( Long userId);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUserByUsername(@Param("keyword") String keyword , Pageable pageable);

    User findUserByUsername(String userName);

    @Query("SELECT COUNT(*) FROM User")
    int countUsers();

    @EntityGraph(attributePaths = "followers")
    @Query("SELECT u FROM User u")
    List<User> findWithFollowersAll(Pageable pageable);
}
