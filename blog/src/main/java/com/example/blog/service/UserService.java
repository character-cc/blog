package com.example.blog.service;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.UnFollowUserDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private CategoryRepository categoryRepository;

    private UserRepository userRepository;

    private RedisTemplate<String , Object> redisTemplate;

    public void saveCategory(List<String> categories , Authentication authentication) {
        Set<Category> categorySet = categoryRepository.findAllByName(categories);
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        user.setCategories(categorySet);
        userRepository.save(user);
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findUserById(id);
        user.getCategories();
        return UserDTO.fromUsertoUserDTO(user);
    }

    public Set<UnFollowUserDTO> getUnFollowUser(Authentication authentication, HttpServletRequest request) {
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        Integer position = (Integer) redisTemplate.opsForValue().get("positionForUnFollow:" + request.getSession().getId());
        if (position == null) position = 0;
        System.out.println("P:" + position);
        Page<User> userPage = userRepository.findAll(PageRequest.of(position, 6));
        if (userPage.isEmpty()) {
            position = 0;
            redisTemplate.opsForValue().set("positionForUnFollow:" + request.getSession().getId(), position, 5, TimeUnit.HOURS);
            userPage = userRepository.findAll(PageRequest.of(position, 6));
        }
        position++;
        redisTemplate.opsForValue().set("positionForUnFollow:" + request.getSession().getId(), position, 5, TimeUnit.HOURS);
        Set<User> userSet = userPage.stream().collect(Collectors.toSet());
        userSet.removeAll(user.getFollowing());
        userSet.remove(user);
        Set<UnFollowUserDTO> unFollowUserDTOSet = new HashSet<>();
        for (User u : userSet) {
            unFollowUserDTOSet.add(UnFollowUserDTO.toDTO(u));
        }

        return unFollowUserDTOSet;
    }


    public void followUser(Long userId, Authentication authentication) {
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserId());
        User user1 = userRepository.findUserById(userId);
        if(user.getFollowing().contains(user1)) {
            user.getFollowing().remove(user1);
        }
        else {
            user.getFollowing().add(user1);
        }
        userRepository.save(user);
    }

}
