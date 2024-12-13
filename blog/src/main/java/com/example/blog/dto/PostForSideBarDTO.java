package com.example.blog.dto;

import com.example.blog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostForSideBarDTO {
    private Long id;

    private String title;

    private String username;

    private String avatar;

    private int totalLikes;

    public static PostForSideBarDTO toDTO(Post post) {
        PostForSideBarDTO dto = new PostForSideBarDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setAvatar(post.getAuthor().getAvatar());
        dto.setUsername(post.getAuthor().getUserName());
        dto.setTotalLikes(post.getLikePost().size());
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostForSideBarDTO that = (PostForSideBarDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, username);
    }
}
