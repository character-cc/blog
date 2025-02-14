package com.example.blog.controller;

import com.example.blog.dto.CommentRequestDTO;
import com.example.blog.dto.PostDetailCommentDTO;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("posts/{postId}/comments")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        PostDetailCommentDTO postDetailCommentDTO = commentService.saveCommentAndRedis(commentRequestDTO);
        return ResponseEntity.ok().body(postDetailCommentDTO);
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<?> likeComment(@PathVariable(value = "commentId") Long commentId) {
        commentService.likeComment(commentId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable(value = "postId") Long postId,
            @RequestParam(value = "page" , defaultValue = "0") Integer page , @RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize) {
        List<PostDetailCommentDTO> postDetailCommentDTOList = commentService.getCommentsByPostId(postId, page, pageSize);
        return ResponseEntity.ok().body(postDetailCommentDTOList);
    }
}
