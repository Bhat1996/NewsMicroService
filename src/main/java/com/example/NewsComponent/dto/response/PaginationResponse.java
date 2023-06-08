package com.example.NewsComponent.dto.response;

import com.example.NewsComponent.dto.vertex.News;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationResponse {
    private Long total;
    private List<News> list;
}
