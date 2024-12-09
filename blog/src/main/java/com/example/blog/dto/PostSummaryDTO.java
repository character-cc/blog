package com.example.blog.dto;

import com.example.blog.entity.Post;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;

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

    private Long day;

    private Long hour;

    private Long likes;

    private Long comments;

    private String imageUrl = "./api/images/post.png";

    public static PostSummaryDTO toDTO(Post post , Long likes, Long comments) {
        PostSummaryDTO dto = new PostSummaryDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor().getUserName());
        dto.setAvatarUser(post.getAuthor().getAvatar());
        Long day = ChronoUnit.DAYS.between(post.getCreatedAt(), LocalDateTime.now());
        dto.setDay(day);
        if(day == 0L) dto.setHour(ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now()));
        dto.setLikes(likes);
        dto.setComments(comments);
        return dto;
    }
}
