package com.example.blog.service;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private CategoryRepository categoryRepository;

    private UserRepository userRepository;

    public void saveCategory(List<String> categories , Authentication authentication) {
        Set<Category> categorySet = categoryRepository.findAllByName(categories);
        User user = userRepository.findUserById(((CustomOidcUser) authentication.getPrincipal()).getUserDTO().getId());
        user.setCategories(categorySet);
        userRepository.save(user);
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findUserById(id);
        return UserDTO.fromUsertoUserDTO(user);
    }
}
