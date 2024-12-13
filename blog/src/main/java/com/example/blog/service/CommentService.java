package com.example.blog.service;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.dto.CommentRequestDTO;
import com.example.blog.dto.PostDetailCommentDTO;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class CommentService {

    private CommentRepository commentRepository;

    private PostRepository postRepository;

    private UserRepository userRepository;

    public PostDetailCommentDTO saveComment(CommentRequestDTO commentRequestDTO , Authentication authentication) {
        Comment comment = new Comment();
        Post post =postRepository.findById(commentRequestDTO.getPostId()).orElse(null);
        if(post==null)  throw new ResourceNotFoundException("Post not found");
        comment.setPost(post);
        comment.setContent(commentRequestDTO.getContent());
        User user = userRepository.findById(((CustomOidcUser) authentication.getPrincipal()).getUserId()).orElse(null);
        comment.setUser(user);
        commentRepository.save(comment);
        return PostDetailCommentDTO.toDTO(comment);
    }

    public boolean likeComment( Long commentId , Authentication authentication) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment==null)  throw new ResourceNotFoundException("Comment not found");
        User user = userRepository.findById(((CustomOidcUser) authentication.getPrincipal()).getUserId()).orElse(null);
        if(user==null)  throw new ResourceNotFoundException("User not found");
        System.out.println("Like " +comment.getLikeComment().size());
        if(comment.getLikeComment().contains(user)) comment.getLikeComment().remove(user);
        else comment.getLikeComment().add(user);
        commentRepository.save(comment);
        System.out.println("Like " +comment.getLikeComment().size());
        return true;

    }
}
