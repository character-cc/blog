package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.PostService;
import com.example.blog.util.KeyForRedis;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    private RedisTemplate<String, String> redisTemplate;


    @PostMapping("/images")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "//images/";
            System.out.println(uploadDir);
            File uploadFile = new File(uploadDir + fileName);
            uploadFile.getParentFile().mkdirs();
            file.transferTo(uploadFile);
            String imageUrl = "http://localhost/api/images/" + fileName;
            Map<String, String> response = new HashMap<>();
            response.put("location", imageUrl);
            redisTemplate.opsForSet().add(KeyForRedis.getKeyForUploadImage(request.getSession().getId()), imageUrl);
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody updatePostDTO updatePostDTO, HttpServletRequest request) {
        log.info("Nhận request: " + updatePostDTO);
        Long id = postService.savePostAndRedis(updatePostDTO, request);
        Map<String, Long> response = new HashMap<>();
        response.put("postId", id);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{postId}/detail")
    private ResponseEntity<?> getPostDetail(@PathVariable Long postId) {
        PostDetailDTO postDetail = postService.getPostDetail(postId);
        return ResponseEntity.ok().body(postDetail);
    }

    @PostMapping(value = "/{postId}/likes")
    public ResponseEntity<?> likePost(@PathVariable(value = "postId") Long postId, Authentication authentication) {
        postService.likePostAndRedis(postId, authentication);
        return ResponseEntity.ok(null);
    }

//    @GetMapping("/follow")
//    public ResponseEntity<?> getPostFollowingUser(Authentication authentication, HttpServletRequest request) {
//        Set<PostForSideBarDTO> postForSideBarDTOSet = postService.getPostFollowingUser(authentication, request);
//        return ResponseEntity.ok(postForSideBarDTOSet);
//    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> getPostSearch(@RequestParam(value = "query") String query, @RequestParam(value = "page" , defaultValue = "0") Integer page,
                                           @RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize) {
        List<PostSummaryDTO> postSummaryDTOSet = postService.getPostSummaryForSearch(query,page,pageSize);
        return ResponseEntity.ok(postSummaryDTOSet);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        postService.deletePostById(postId);
        return ResponseEntity.ok(null);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id, @RequestBody updatePostDTO updatePostDTO , HttpServletRequest request) {
        updatePostDTO.setId(id);
        Long postId = postService.editPost(updatePostDTO,request);
        Map<String, Long> response = new HashMap<>();
        response.put("postId", postId);
        return ResponseEntity.ok(response);
    }

}
