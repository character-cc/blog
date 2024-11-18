package com.example.blog.controller;

import com.example.blog.entity.Post;
import com.example.blog.entity.UserSecurity;
import com.example.blog.service.PostRedisService;
import com.example.blog.service.PostService;
import com.example.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    RedisService redisService;

    @Autowired
    PostRedisService postRedisService;

    @GetMapping("/create-post")
    public String showCreateForm(){
        return "create_post";
    }

    @PostMapping("/create-post")
    public String processCreateForm(@RequestParam("title") String title , @RequestParam("content") String content , Authentication authentication , Model model){
        Post post = new Post(title,content,  ((UserSecurity) authentication.getPrincipal()).getUser());
        postService.addPost(post);
        postRedisService.addPOST_VOTE_COUNT_KEY(post.getId(),0L);
        return "redirect:/mypost?success=create";
    }

    @GetMapping("/mypost")
    public String showTablePost(@RequestParam(value = "success", required = false) String success , Model model ,Authentication authentication){
        if(success != null) {
            if(success.equals("delete")){
                model.addAttribute("success" , "Xóa Thành Công");
            }
            if(success.equals("create")){
                model.addAttribute("success" , "Tạo Thành Công");
            }
            if(success.equals("update")){
                model.addAttribute("success" , "Thay đổi Thành Công");
            }
        }
        List<Post> posts = postService.getAllPostById(((UserSecurity) authentication.getPrincipal()).getUser().getId());
        model.addAttribute("userPosts" , posts);
        return "posts";
    }

    @PostMapping("delete-post/{id}")
    public String delete_post(@PathVariable("id") Long postId , Model model){
        postService.deletePostById(postId);
        return "redirect:/mypost?success=delete";
    }

    @PostMapping("/post/update/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post , Model model) {
        Post post1 = postService.getPostById(post.getId());
        post1.setContent(post.getContent());
        post1.setTitle(post.getTitle());
       postService.updatePost(post1);
        return "redirect:/mypost?success=update";
    }

    @GetMapping("edit-post/{id}")
    public String showFormPostEdit(@PathVariable("id") Long postId , Model model){
        Post post = postService.getPostById(postId);
        model.addAttribute("post",post);
        return "edit_post";
    }

    @GetMapping("/post-detail/{id}")
    public String postDetail(@PathVariable("id") Long postId , Model model , Authentication authentication){
        Post post_detail = postService.getPostById(postId);
        Set<Object> topPostIds = postRedisService.getTopVotedPostIds(4);
        List<Long> postIds = topPostIds.stream()
                .filter(id -> id instanceof Long)
                .map(id -> (Long) id)
                .collect(Collectors.toList());
        List<Post> posts = postService.getPostsByIds(postIds);
        List<Long> postList = new ArrayList<>();
        postList.add(postId);
        Map<Long, Boolean> userVoteStatus = postRedisService.getUserVoteStatus(postIds,authentication);
        model.addAttribute("post_detail" , post_detail);
        model.addAttribute("posts" ,posts);
        model.addAttribute("userVoteStatus" , userVoteStatus);
        Long voteCount = postRedisService.getVoteCountForPost(postId);
        model.addAttribute("voteCount" , voteCount);
        return "post_detail";
    }
}
