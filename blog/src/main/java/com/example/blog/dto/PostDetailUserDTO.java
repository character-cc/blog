package com.example.blog.dto;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailUserDTO {

    private Long id;

    private String username;

    private String avatar;

    public static PostDetailUserDTO toDTO(User user) {
        PostDetailUserDTO dto = new PostDetailUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUserName());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

}
