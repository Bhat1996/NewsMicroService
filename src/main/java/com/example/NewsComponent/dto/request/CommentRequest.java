package com.example.NewsComponent.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String text;
}
