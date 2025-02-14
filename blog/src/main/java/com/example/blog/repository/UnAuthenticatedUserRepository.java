package com.example.blog.repository;

import com.example.blog.entity.UnAuthenticatedUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UnAuthenticatedUserRepository extends JpaRepository<UnAuthenticatedUser, Long> {

    @EntityGraph(attributePaths = "categories")
    Optional<UnAuthenticatedUser> findWithCategoriesById(String id);
}
