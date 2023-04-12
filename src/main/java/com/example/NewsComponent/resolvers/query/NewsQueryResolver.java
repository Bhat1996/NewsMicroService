package com.example.NewsComponent.resolvers.query;

import com.example.NewsComponent.dto.request.DiscoverNewsInput;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.dto.response.Pagination;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.SearchType;
import com.example.NewsComponent.exceptions.GeneralBadRequestException;
import com.example.NewsComponent.service.query.NewsQueryService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.example.NewsComponent.enums.NewsStatus.PUBLISHED;
import static com.example.NewsComponent.enums.SearchType.DISCOVER;
import static com.example.NewsComponent.enums.SearchType.FOR_YOU;

@Service
public class NewsQueryResolver implements GraphQLQueryResolver {
    private final NewsQueryService newsQueryService;

    public NewsQueryResolver(NewsQueryService newsQueryService) {
        this.newsQueryService = newsQueryService;
    }

    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter) {
        return newsQueryService.getAllNews(newsStatus, paginationFilter, newsFilter);
    }

    public NewsResponse getNewsById(String id) {
        return newsQueryService.getNewsById(id);
    }

    // TODO: 12-04-2023 check here later make query for interest Ids
    public Pagination<NewsResponse> discoverNews(DiscoverNewsInput discoverNewsInput,
                                                 PaginationFilter paginationFilter) {
        NewsFilter newsFilter = new NewsFilter();
        SearchType searchType = discoverNewsInput.getSearchType();
        if (searchType.equals(FOR_YOU)) {
            Set<String> interests = discoverNewsInput.getInterestIds();
            if (interests.isEmpty()) {
                throw new GeneralBadRequestException("At Least Provide 1 Interest To Discover News");
            }
            return newsQueryService.getAllNews(PUBLISHED, paginationFilter, newsFilter);
        } else {
            if(searchType.equals(DISCOVER)) {
                return newsQueryService.getAllNews(PUBLISHED, paginationFilter, newsFilter);
            }
        }

        return null;
    }
}


