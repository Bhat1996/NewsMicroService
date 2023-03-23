package com.example.NewsComponent.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Part;
import java.util.List;
@Getter
@Setter
public class FileInputWithPart {
    private List<Part> images;
    private List<Part> audios;
    private List<Part> videos;
    private List<Part> documents;
}
