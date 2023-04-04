package com.example.NewsComponent.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentRequest {
    @NotBlank
    private String newsId;
    @NotBlank
    private String text;
}
