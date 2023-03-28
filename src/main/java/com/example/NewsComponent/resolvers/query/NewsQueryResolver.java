package com.example.NewsComponent.resolvers.query;

import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.dto.response.Pagination;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.service.query.NewsQueryService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;

@Service
public class NewsQueryResolver implements GraphQLQueryResolver {
    private  final NewsQueryService newsQueryService;

    public NewsQueryResolver(NewsQueryService newsQueryService) {
        this.newsQueryService = newsQueryService;
    }

    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter){
        return newsQueryService.getAllNews(newsStatus, paginationFilter, newsFilter);
    }
}
