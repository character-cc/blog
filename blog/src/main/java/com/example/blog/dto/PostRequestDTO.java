package com.example.blog.dto;

import com.example.blog.entity.Post;
import com.example.blog.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
public class PostRequestDTO {

    private String title;
    private List<String> categories;
    private String content;

//    public static PostRequestDTO toPost(PostRequestDTO postRequestDTO) {
//           Post post = new Post();
//           post.setTitle(postRequestDTO.getTitle());
//           post.setContent(postRequestDTO.getContent());
//           return postRequestDTO;
//    }
}

