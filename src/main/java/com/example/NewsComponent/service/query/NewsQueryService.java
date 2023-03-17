package com.example.NewsComponent.service.query;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.dto.response.PageInfo;
import com.example.NewsComponent.dto.response.Pagination;
import com.example.NewsComponent.dto.response.PaginationResponse;
import com.example.NewsComponent.enums.NewsStatus;

import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Service
public class NewsQueryService {

    private final ArangoOperations arangoOperations;
    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsQueryService(ArangoOperations arangoOperations,
                            NewsRequestResponseMapper newsRequestResponseMapper) {
        this.arangoOperations = arangoOperations;
        this.newsRequestResponseMapper = newsRequestResponseMapper;

    }

    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter) {

            String query = """
                    LET list = (
                        FOR doc IN ${news}
                        FILTER doc.newsStatus == '${newsStatus}'
                        SORT doc.newsPublishDate ${order}
                        LIMIT ${skip}, ${limit}
                        RETURN doc
                    )
                    LET total = (
                        FOR doc IN news
                        FILTER doc.newsStatus == '${newsStatus}'
                        COLLECT WITH COUNT INTO size
                        RETURN size
                    )
                                      
                    RETURN {
                        list: list,
                        total: first(total)
                    }
                    """;

            Map<String, String> queryParams = Map.of(
                    "news", NEWS,
                    "newsStatus", newsStatus.toString(),
                    "order", paginationFilter.getOrder().toString(),
                    "skip", paginationFilter.skip().toString(),
                    "limit", paginationFilter.getLimit().toString()
            );

            String finalQuery = new StringSubstitutor(queryParams).replace(query);
            ArangoCursor<PaginationResponse> cursor =
                    arangoOperations.query(finalQuery, PaginationResponse.class);
            try(cursor){
                Optional<PaginationResponse> first = cursor.stream().findFirst();
                if (first.isPresent()){
                    PaginationResponse paginationResponse = first.get();

                    Long total = paginationResponse.getTotal();
                    PageInfo pageInfo = PageInfo.ofResult(total, paginationFilter);

                    List<News> newsList = paginationResponse.getList();
                    List<NewsResponse> responseList =
                            newsList.stream().map(newsRequestResponseMapper::getNewsResponse).toList();
                    return new Pagination<>(responseList, pageInfo);
                }else {
                    throw new RuntimeException("No data found");
                }
            }catch (IOException ioException){
                throw new RuntimeException(ioException);
            }
//            ArangoCursor<News> cursor = arangoOperations.query(finalQuery, News.class);
//            List<News> news = cursor.asListRemaining();
//            //return news.stream().map(news1 -> newsMapper.getNewsResponse(news1)).collect(Collectors.toList());
//            return news.stream().map(newsRequestResponseMapper::getNewsResponse).toList();

    }


}
