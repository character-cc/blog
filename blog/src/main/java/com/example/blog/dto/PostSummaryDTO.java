package com.example.blog.dto;

import com.example.blog.entity.Post;
import com.example.blog.util.ExtractHtmlContent;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostSummaryDTO {

    private Long id;

    private String title;

    private String content;

    private String avatarUser;

    private String author;

    private Long likes;

    private Long comments;

    private String imageUrl = "http://localhost/api/images/post.png";

    public static PostSummaryDTO toDTO(Post post , Long likes, Long comments) {
        PostSummaryDTO dto = new PostSummaryDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(ExtractHtmlContent.getSummaryFromHtml(post.getContent() , 20));
        dto.setAuthor(post.getAuthor().getUserName());
        dto.setAvatarUser(post.getAuthor().getAvatar());
        dto.setLikes(likes);
        dto.setComments(comments);
        if(post.getImages().size() > 0) dto.setImageUrl(post.getImages().get(0));
        return dto;
    }
}
