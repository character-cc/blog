package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnFollowUserDTO {

    private Long id;

    private String username;

    private boolean follow;

    private String avatar;

    public static UnFollowUserDTO toDTO(User user) {
        UnFollowUserDTO dto = new UnFollowUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUserName());
        dto.setFollow(false);
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
