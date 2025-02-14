package com.example.blog.dto;

import com.example.blog.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDTO {


    @NotNull(message = "Tên đăng nhập không được bỏ trống")
    @Size(min = 6 , message = "Tên đăng nhập phải ít nhất 6 kí tự")
    private String username;

    @NotNull(message = "Mật khẩu không được bỏ trống")
    @Size(min = 6 , message = "Mật khẩu phải ít nhất 6 kí tự")
    private String password;

    @NotNull(message = "Email không được bỏ trống")
    @Email(message = "Không đúng định dạng email")
    private String email;

    @NotNull(message = "Tên không được bỏ trống")
    @Size(min = 6 , message = "Tên phải ít nhất 4 kí tự")
    private String name;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatar("http://localhost/api/images/avatar.png");
        user.setName(name);
        return user;
    }
}
