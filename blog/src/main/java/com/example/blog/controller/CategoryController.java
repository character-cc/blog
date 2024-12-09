package com.example.blog.controller;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/categories")
    public ResponseEntity<?> categories() {
        try{
            List<Category> categoryList = categoryService.findAll();
            return ResponseEntity.ok(categoryList);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-categories")
    public ResponseEntity<?> uploadCategories(@RequestBody Map<String, List<String>> body , Authentication auth) {
        try {
            List<String> categories = body.get("categories");
            userService.saveCategory(categories,auth);
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
