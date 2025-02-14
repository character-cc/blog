package com.example.blog.controller;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> categories() {
        List<Category> categoryList = categoryService.findAll();
        return ResponseEntity.ok(categoryList);
    }

    @PostMapping
    public ResponseEntity<?> uploadCategories(@RequestBody Map<String, List<String>> body) {
        Set<String> categories = body.get("categories").stream().collect(Collectors.toSet());
        userService.saveCategory(categories);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }


}
