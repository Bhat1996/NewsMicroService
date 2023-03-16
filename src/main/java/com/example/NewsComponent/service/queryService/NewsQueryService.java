package com.example.NewsComponent.service.queryService;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;

import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NewsQueryService {

    private final ArangoOperations arangoOperations;
    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsQueryService(ArangoOperations arangoOperations, NewsRequestResponseMapper newsRequestResponseMapper) {
        this.arangoOperations = arangoOperations;
        this.newsRequestResponseMapper = newsRequestResponseMapper;

    }

    public List<NewsResponse> getAllNews(NewsStatus newsStatus) {

            String query = """
                    for doc in news
                    filter doc.newsStatus == '${newsStatus}'
                    return doc
                    """;
            Map<String, String> queryParams = Map.of("newsStatus", newsStatus.toString());
            String finalQuery = new StringSubstitutor(queryParams).replace(query);
            ArangoCursor<News> cursor = arangoOperations.query(finalQuery, News.class);
            List<News> news = cursor.asListRemaining();
            //return news.stream().map(news1 -> newsMapper.getNewsResponse(news1)).collect(Collectors.toList());
            return news.stream().map(newsRequestResponseMapper::getNewsResponse).toList();

    }


}
