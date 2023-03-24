package com.example.NewsComponent.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FileDto {
    private List<FileKeyWithOriginalName> imageKeys;
    private List<FileKeyWithOriginalName> videoKeys;
    private List<FileKeyWithOriginalName> audioKeys;
    private List<FileKeyWithOriginalName> documentKeys;
}
