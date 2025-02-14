package com.example.blog.dto;

import com.example.blog.entity.Post;
import com.example.blog.util.ExtractHtmlContent;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PostSummaryDTO {

    private Long id;

    private String title;

    private String content;

    private String avatarUser;

    private String author;

    private Integer totalLikes;

    private Integer totalComments;

    private String imageUrl ;

    public static PostSummaryDTO fromPost(Post post , Integer likes, Integer comments) {
        PostSummaryDTO dto = new PostSummaryDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(ExtractHtmlContent.getSummaryFromHtml(post.getContent() , 20));
        dto.setAuthor(post.getAuthor().getName());
        dto.setAvatarUser(post.getAuthor().getAvatar());
        dto.setTotalLikes(likes);
        dto.setTotalComments(comments);
        String image = post.getImages().stream().findFirst().orElse(null);
        if(!post.getImages().isEmpty()) dto.setImageUrl(image);
        else dto.setImageUrl("http://localhost/api/images/post.png");
        return dto;
    }
}
