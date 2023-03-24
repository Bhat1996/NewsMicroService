package com.example.NewsComponent.service.query;

import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.Pagination;
import org.springframework.stereotype.Service;


@Service
public class NewsQueryService {

    private final NewsRepository newsRepository;

    public NewsQueryService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }


    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter) {
        return newsRepository.getAllNews(newsFilter, paginationFilter, newsStatus);
    }


}
