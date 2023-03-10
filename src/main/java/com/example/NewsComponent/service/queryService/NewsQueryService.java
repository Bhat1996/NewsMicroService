package com.example.NewsComponent.service.queryService;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;

import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsQueryService {

    private final ArangoOperations arangoOperations;
    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsQueryService(ArangoOperations arangoOperations, NewsRequestResponseMapper newsRequestResponseMapper) {
        this.arangoOperations = arangoOperations;
        this.newsRequestResponseMapper = newsRequestResponseMapper;

    }

    public List<NewsResponse> getAllNews(NewsStatus newsStatus) {
        if (newsStatus.equals(NewsStatus.PUBLISHED)) {
            String query = """
                    for doc in NEWS
                    filter doc.newsStatus == "PUBLISHED"
                    return doc
                    """;
            ArangoCursor<News> cursor = arangoOperations.query(query, News.class);
            List<News> news = cursor.asListRemaining();
            //return news.stream().map(news1 -> newsMapper.getNewsResponse(news1)).collect(Collectors.toList());
            return news.stream().map(newsRequestResponseMapper::getNewsResponse).toList();
        } else {
            String query = """
                    for doc in NEWS
                    filter doc.newsStatus == "DRAFT"
                    return doc
                    """;
            ArangoCursor<News> cursor = arangoOperations.query(query, News.class);
            List<News> news = cursor.asListRemaining();
            //return news.stream().map(news1 -> newsMapper.getNewsResponse(news1)).collect(Collectors.toList());
            return news.stream().map(newsRequestResponseMapper::getNewsResponse).toList();
        }
    }


}
