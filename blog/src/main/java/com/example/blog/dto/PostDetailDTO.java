package com.example.blog.dto;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailDTO {

    private Long id;

    private String title;

    private String content;

    private PostDetailUserDTO author;

    private List<String> imageUrl;

    private Set<PostDetailCommentDTO> comments;

    private LocalDateTime createdAt;

    private int totalLikes;

    private Set<String> categories;

    private boolean likedByCurrentUser;

    public static PostDetailDTO toDTO(Post post) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(PostDetailUserDTO.toDTO(post.getAuthor()));
        dto.setImageUrl(post.getImages());
        dto.setComments(post.getComments().stream().map(PostDetailCommentDTO::toDTO).collect(Collectors.toSet()));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setTotalLikes(post.getLikePost().size());
        dto.setLikedByCurrentUser(false);
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomOidcUser) {
            CustomOidcUser customOidcUser = (CustomOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for(PostDetailCommentDTO comment : dto.getComments()){
                if(comment.getUser().getId().equals(customOidcUser.getUserId())) dto.setLikedByCurrentUser(true);
            }
        }
        dto.setCategories(post.getCategories().stream().map(category -> String.valueOf(category.getName())).collect(Collectors.toSet()));
        return dto;
    }
}