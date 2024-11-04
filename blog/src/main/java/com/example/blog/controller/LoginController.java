package com.example.blog.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error" , required = false) String error , Model model , Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        if(error != null){
            model.addAttribute("error","Invalid username or password. Please try again.");
        }
        return "login.html";
    }
}
