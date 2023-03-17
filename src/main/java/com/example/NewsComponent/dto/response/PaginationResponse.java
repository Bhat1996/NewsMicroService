package com.example.NewsComponent.dto.response;

import com.example.NewsComponent.domain.News;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationResponse {
    private Long total;
    private List<News> list;
}
