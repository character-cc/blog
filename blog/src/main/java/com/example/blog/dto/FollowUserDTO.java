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
public class FollowUserDTO {

    private Long id;

    private String username;

    private boolean follow;

    private String avatar;

    public static FollowUserDTO toDTO(User user) {
        return toDTO(user, false);
    }

    public static FollowUserDTO toDTO(User user, boolean follow) {
        FollowUserDTO dto = new FollowUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUserName());
        dto.setFollow(follow);
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
