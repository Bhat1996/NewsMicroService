package com.example.NewsComponent.dto.internal;

import com.example.NewsComponent.dto.response.FileUrl;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileResults {
    private String id;
    private List<FileUrl> imageUrls;
    private List<FileUrl> audioUrls;
    private List<FileUrl> videoUrls;
    private List<FileUrl> documentUrls;


}
