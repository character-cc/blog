package com.example.blog.dto;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailCommentDTO {

    private Long id;

    private String content;

    private PostDetailUserDTO user;

    private LocalDateTime createdAt;

    private int totalLikes;

    private boolean likedByCurrentUser;

    public static PostDetailCommentDTO toDTO(Comment comment) {
        PostDetailCommentDTO dto = new PostDetailCommentDTO();
        dto.setLikedByCurrentUser(false);
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomOidcUser) {
            CustomOidcUser customOidcUser = (CustomOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for(User user : comment.getLikeComment()) if(user.getId().equals(customOidcUser.getUserId()) ) dto.setLikedByCurrentUser(true);
        }

       dto.setId(comment.getId());
       dto.setContent(comment.getContent());
       dto.setCreatedAt(comment.getCreatedAt());
       dto.setTotalLikes(comment.getLikeComment().size());
       dto.setUser(PostDetailUserDTO.toDTO(comment.getUser()));
       return dto;
    }
}
