package com.example.NewsComponent.repository;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.PageInfo;
import com.example.NewsComponent.dto.response.Pagination;
import com.example.NewsComponent.dto.response.PaginationResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.exceptions.ResourceNotFoundException;
import com.example.NewsComponent.mapper.FileResponseMapper;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.helper.NewsQueryGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class NewsRepository {

    private final ArangoOperations arangoOperations;
    private final ArangoConverter arangoConverter;
    private final NewsRequestResponseMapper newsRequestResponseMapper;
    private final NewsQueryGenerator newsQueryGenerator;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final FileResponseMapper fileResponseMapper;


    public News getNewsById(String id) {
        String query = """
                FOR doc IN ${coll}
                FILTER doc._key == '${id}'
                RETURN doc
                """;
        Map<String, String> queryFiller = Map.of(
                "coll", "news",
                "id", id);
        String finalQuery = new StringSubstitutor(queryFiller).replace(query);
        ArangoCursor<News> cursor = arangoOperations.query(finalQuery, News.class);
        Optional<News> first = cursor.stream().findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new RuntimeException("News with given id not found");
        }
    }

    @SneakyThrows
    public Pagination<NewsResponse> getAllNews(NewsFilter newsFilter,
                                               PaginationFilter paginationFilter,
                                               NewsStatus newsStatus) {

        String finalQuery = newsQueryGenerator.getQuery(newsFilter, paginationFilter, newsStatus);
        ArangoCursor<PaginationResponse> cursor =
                arangoOperations.query(finalQuery, PaginationResponse.class);
        try (cursor) {
            Optional<PaginationResponse> first = cursor.stream().findFirst();
            if (first.isPresent()) {
                PaginationResponse paginationResponse = first.get();

                Long total = paginationResponse.getTotal();
                PageInfo pageInfo = PageInfo.ofResult(total, paginationFilter);

                List<News> newsList = paginationResponse.getList();

                List<NewsResponse> responseList =
                        new ArrayList<>();
                for (News news : newsList) {
                    NewsResponse newsResponse = newsRequestResponseMapper.getNewsResponse(news);
                    NewsResponse newsResponseWithFiles = fileResponseMapper.getNewsResponseWithFiles
                            (news.getId(), newsResponse);
                    responseList.add(newsResponseWithFiles);

                }
                return new Pagination<>(responseList, pageInfo);
            } else {
                // TODO: 21-03-2023 give proper exception
                throw new ResourceNotFoundException("No data found");
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public News saveNews(ArangoDatabase arangoDatabase, String transactionId, News news) {

        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("news")
                .insertDocument(arangoConverter.write(news),
                        new DocumentCreateOptions()
                                .streamTransactionId(transactionId)
                                .returnNew(true));
        return arangoConverter.read(News.class, createEntity.getNew());
    }

    public News updateNews(ArangoDatabase arangoDatabase, String transactionId, News news) {
        DocumentUpdateEntity<VPackSlice> updatedNews = arangoDatabase.collection("news")
                .updateDocument(news.getId(), arangoConverter.write(news),
                        new DocumentUpdateOptions()
                                .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(News.class, updatedNews.getNew());

    }

}
