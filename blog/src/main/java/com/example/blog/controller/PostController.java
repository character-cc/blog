package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import com.example.blog.service.PostService;
import com.example.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    RedisService redisService;

    private final String POST_VOTE_COUNT_KEY = "post:voteCounts";


    @GetMapping("/create")
    public String showCreateForm(){
        return "create_post";
    }

    @PostMapping("/post")
    public String processCreateForm(@RequestParam("title") String title , @RequestParam("content") String content , Authentication authentication , Model model){
        Post post = new Post(title,content,  ((UserSecurity) authentication.getPrincipal()).getUser());
        postService.addPost(post);
        redisService.addPOST_VOTE_COUNT_KEY(post.getId(),0L);
        model.addAttribute("success" ,"Thành Công");
        return "create_post";
    }

    @GetMapping("mypost")
    public String showTablePost(@RequestParam(value = "success", required = false) String success , Model model ,Authentication authentication){
        if(success != null) model.addAttribute("success" , "Thành công");
        List<Post> posts = postService.getAllPostById(((UserSecurity) authentication.getPrincipal()).getUser().getId());
        model.addAttribute("userPosts" , posts);
        return "posts";
    }

    @PostMapping("delete-post/{id}")
    public String delete_post(@PathVariable("id") Long postId , Model model){
        postService.deletePostById(postId);
        return "redirect:/mypost?success=true";
    }

    @PostMapping("/post/update/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post , Model model) {
        Post post1 = postService.getPostById(post.getId());
        post1.setContent(post.getContent());
        post1.setTitle(post.getTitle());
       postService.updatePost(post1);
        return "redirect:/mypost?success=true";
    }

    @GetMapping("edit-post/{id}")
    public String showFormPostEdit(@PathVariable("id") Long postId , Model model){

        Post post = postService.getPostById(postId);
        model.addAttribute("post",post);
        return "edit_post";
    }

}
