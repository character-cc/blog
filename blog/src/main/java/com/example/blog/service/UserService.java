package com.example.blog.service;

import com.example.blog.dto.FollowUserDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.util.UserContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private CategoryRepository categoryRepository;

    private UserRepository userRepository;

    private UserContext userContext;


    private EntityManager entityManager;

    public void logEntityStates() {
        Session session = entityManager.unwrap(Session.class);
        SessionImplementor sessionImplementor = session.unwrap(SessionImplementor.class);
        PersistenceContext persistenceContext = sessionImplementor.getPersistenceContext();
        persistenceContext.managedEntitiesIterator().forEachRemaining(entity -> {
            EntityEntry entityEntry = persistenceContext.getEntry(entity);
            if (entityEntry != null) {
                String entityName = entityEntry.getPersister().getEntityName();
                Object entityId = entityEntry.getId();
                String status = entityEntry.getStatus().name();
                boolean isReadOnly = persistenceContext.isReadOnly(entity);
                System.out.println("Entity: " + entityName +
                        ", ID: " + entityId +
                        ", Status: " + status +
                        ", ReadOnly: " + isReadOnly);
            }
        });
    }

    @Transactional
    public void saveCategory(Set<String> categories) {
        Set<Category> categorySet = categoryRepository.findAllByName(categories).stream().collect(Collectors.toSet());
        if(categorySet.isEmpty()) throw new EntityNotFoundException("Danh mục có thể đã bị xóa hoặc không tồn tại");
        User user = userRepository.findWithCategoriesById(userContext.getUserId());
        user.setCategories(categorySet);
        logEntityStates();
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findWithCategoriesById(id);
        return UserDTO.fromUser(user);
    }



    @Transactional(readOnly = true)
    public Set<FollowUserDTO> getFollow(int page, int pageSize) {
        int countUser = userRepository.countUsers();
        int totalpage = countUser / pageSize;
        User currentUser = userRepository.findById(userContext.getUserId()).orElse(null);
        List<User> userSet = userRepository.findWithFollowersAll(PageRequest.of(page,pageSize));
        Set<FollowUserDTO> followUserDTOSet = userSet.stream().map(user -> {
            boolean isFollower = false;
            if(user.getFollowers().contains(currentUser)) isFollower = true;
            return FollowUserDTO.fromUser(user,isFollower);
        }).collect(Collectors.toSet());
        logEntityStates();
        return followUserDTOSet;
    }


    @Transactional
    public void followUser(Long userId) {
        User user = userRepository.findById(userContext.getUserId()).orElseThrow();
        User user1 = userRepository.findById(userId).orElseThrow(

        );
        if(user.getFollowing().contains(user1)) {
            user.getFollowing().remove(user1);
        }
        else {
            user.getFollowing().add(user1);
        }
        logEntityStates();
    }

    public Long saveUser(User user) {
        return userRepository.save(user).getId();
    }

}
