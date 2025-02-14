package com.example.blog.dto;

import com.example.blog.config.CustomOidcUser;
import com.example.blog.config.UserSecurity;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
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

    private Set<String> imageUrl;

    private LocalDateTime createdAt;

    private int totalLikes;

    private Set<Category> categories;

    private boolean likedByCurrentUser;

    public static PostDetailDTO fromPost(Post post , int totalLikes , boolean isLikedByCurrentUser) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
            dto.setAuthor(PostDetailUserDTO.toDTO(post.getAuthor()));
        dto.setImageUrl(post.getImages());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setTotalLikes(totalLikes);
        dto.setLikedByCurrentUser(false);
        dto.setCategories(post.getCategories());
        return dto;
    }
}
