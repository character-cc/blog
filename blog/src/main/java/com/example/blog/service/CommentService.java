package com.example.blog.service;

import com.example.blog.dto.CommentRequestDTO;
import com.example.blog.dto.PostDetailCommentDTO;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.util.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private RedisService redisService;

    private CommentRepository commentRepository;

    private PostRepository postRepository;

    private UserRepository userRepository;

    private ApplicationContext applicationContext;

    private UserContext userContext;

    public CommentService proxy(){
        return applicationContext.getBean(CommentService.class);
    }

    @Transactional
    public Comment saveComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = new Comment();
        Post post = postRepository.getReferenceById(commentRequestDTO.getPostId());
        comment.setPost(post);
        comment.setContent(commentRequestDTO.getContent());
        User user = userRepository.getReferenceById(userContext.getUserId());
        comment.setUser(user);
        commentRepository.save(comment);
        return comment;
    }

    public PostDetailCommentDTO saveCommentAndRedis(CommentRequestDTO commentRequestDTO){
        PostService postService = applicationContext.getBean(PostService.class);
        Comment comment = proxy().saveComment(commentRequestDTO);
        Post post = postRepository.findById(commentRequestDTO.getPostId()).orElseThrow(
                () ->  postService.postNotFoundException(commentRequestDTO.getPostId())
        );
        redisService.incrementTotalPostComment(post.getId() , 1);
        postService.recalculateScoreAfterUpdatePost(post);
        User user = userContext.getUserSummary().toUser();
        comment.setUser(user);
        return PostDetailCommentDTO.fromComment(comment);
    }

    @Transactional
    public void likeComment(Long commentId) {
            Comment comment = commentRepository.findWithLikesById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Bình luận không được tìm thấy với id: " + commentId)
        );
        User user = userRepository.findById(userContext.getUserId()).orElse(null);
        if(comment.getLikes().contains(user)) comment.getLikes().remove(user);
        else comment.getLikes().add(user);
    }

    @Transactional(readOnly = true)
    public List<PostDetailCommentDTO> getCommentsByPostId(Long postId , Integer page , Integer size) {
        return commentRepository.findWithUserAndLikesByPostId(postId, PageRequest.of(page,size)).stream().map(comment -> {
            return PostDetailCommentDTO.fromComment(comment);
        }).collect(Collectors.toList());
    }
}
