package com.example.blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentRequestDTO {

    @NotNull(message = "Id bài viết không được bỏ trống")
    private Long postId;

    @NotNull(message = "Bình luận không được bỏ trống")
    private String content;
}
