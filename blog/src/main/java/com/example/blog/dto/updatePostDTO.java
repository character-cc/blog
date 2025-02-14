package com.example.blog.dto;

import com.example.blog.entity.Post;
import lombok.*;
import org.springframework.validation.annotation.Validated;


import jakarta.validation.constraints.Size;
import java.util.Set;
import jakarta.validation.constraints.NotNull;



@Data
public class updatePostDTO {

    private Long id;

    @NotNull(message = "Tiêu đề không được để trống")
    @Size(min = 5, message = "Tiêu đề phải có ít nhất 5 ký tự")
    String title;

    @NotNull(message = "Nội dung không được để trống")
    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
    String content;

    @NotNull(message = "Danh mục không được để chống")
    private Set<String> categories;

    public Post toPost() {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setContent(content);
        return post;
    }

    public static updatePostDTO fromPost(Post post) {
        updatePostDTO updatePostDTO = new updatePostDTO();
        updatePostDTO.setId(post.getId());
        updatePostDTO.setTitle(post.getTitle());
        updatePostDTO.setContent(post.getContent());
        post.getCategories().forEach(category -> updatePostDTO.getCategories().add(category.getName()));
        return updatePostDTO;
    }


}

