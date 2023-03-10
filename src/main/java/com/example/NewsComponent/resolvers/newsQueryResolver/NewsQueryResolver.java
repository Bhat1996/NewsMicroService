package com.example.NewsComponent.resolvers.newsQueryResolver;

import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.service.queryService.NewsQueryService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NewsQueryResolver implements GraphQLQueryResolver {
    private  final NewsQueryService newsQueryService;

    public NewsQueryResolver(NewsQueryService newsQueryService) {
        this.newsQueryService = newsQueryService;
    }

    public List<NewsResponse> getAllNews(NewsStatus newsStatus){
        return newsQueryService.getAllNews(newsStatus);
    }
}
