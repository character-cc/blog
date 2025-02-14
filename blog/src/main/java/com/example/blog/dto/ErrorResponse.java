package com.example.blog.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private Object message;

    public ErrorResponse(Object message)
    {
        super();
        this.message = message;
    }
}
