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

    private String name;

    private boolean follow;

    private String avatar;

    public static FollowUserDTO fromUser(User user) {
        return fromUser(user, false);
    }

    public static FollowUserDTO fromUser(User user, boolean follow) {
        FollowUserDTO dto = new FollowUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setFollow(follow);
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
