package com.example.NewsComponent.service.query;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.mapper.FileResponseMapper;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.Pagination;
import org.springframework.stereotype.Service;


@Service
public class NewsQueryService {

    private final NewsRepository newsRepository;
    private  final NewsRequestResponseMapper newsRequestResponseMapper;
    private  final FileResponseMapper fileResponseMapper;

    public NewsQueryService(NewsRepository newsRepository,
                            NewsRequestResponseMapper newsRequestResponseMapper,
                            FileResponseMapper fileResponseMapper) {
        this.newsRepository = newsRepository;
        this.newsRequestResponseMapper = newsRequestResponseMapper;
        this.fileResponseMapper = fileResponseMapper;
    }

    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter) {
        return newsRepository.getAllNews(newsFilter, paginationFilter, newsStatus);
    }

    public NewsResponse getNewsById(String id){
        News newsById = newsRepository.getNewsById(id);
        NewsResponse newsResponse = newsRequestResponseMapper.getNewsResponse(newsById);
      return   fileResponseMapper.getNewsResponseWithFiles(newsById.getId(),newsResponse);
    }



}
