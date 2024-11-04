package com.example.blog.controller;

import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignUpController {

    @Autowired
    UserService userService;

    @GetMapping("signup")
    public String showSignUpForm(@RequestParam(required = false) String error , Model model){
        if(error != null){
            model.addAttribute("error", "Username already exists.");
        }
        return "sign_up";
    }

    @PostMapping("signup")
    public String processSignUp(@RequestParam String username , @RequestParam String password  , HttpServletRequest request, HttpServletResponse response){
        boolean errorBoolean = userService.processSignup(username,password , request , response);
        if(errorBoolean) {
            return "redirect:/sign_up?error=true";
        }
        return "redirect:/home";
    }

}
