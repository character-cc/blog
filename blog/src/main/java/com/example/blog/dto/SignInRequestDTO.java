package com.example.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignInRequestDTO {

    @NotNull(message = "Tên đăng nhập không được bỏ trống")
    @Size(min = 6 , message = "Tên đăng nhập phải ít nhất 6 kí tự")
    private String username;

    @NotNull(message = "Mật khẩu không được bỏ trống")
    @Size(min = 6 , message = "Mật khẩu phải ít nhất 6 kí tự")
    private String password;
}
