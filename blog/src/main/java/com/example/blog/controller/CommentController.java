package com.example.blog.controller;

import com.example.blog.dto.CommentRequestDTO;
import com.example.blog.dto.PostDetailCommentDTO;
import com.example.blog.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CommentController {



    private CommentService commentService;

    @PostMapping(value = "/comment/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequestDTO , Authentication authentication) {
        try {
            PostDetailCommentDTO postDetailCommentDTO = commentService.saveComment(commentRequestDTO,authentication);
            return ResponseEntity.ok().body(postDetailCommentDTO);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "like_comment")
    public ResponseEntity<?> likeComment(@RequestBody Map<String, Long> commentRequestDTO , Authentication authentication) {
           if(authentication == null) return ResponseEntity.status(401).body(null);
           Long commentId = commentRequestDTO.get("commentId");
           boolean success = commentService.likeComment(commentId,authentication);
           Map<String, Boolean> response = new HashMap<>();
           response.put("success", success);
           return ResponseEntity.ok().body(response);
    }
}
