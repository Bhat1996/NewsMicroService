package com.example.NewsComponent.repository;

import com.amazonaws.util.json.Jackson;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.dto.vertex.News;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.dto.response.PageInfo;
import com.example.NewsComponent.dto.response.Pagination;
import com.example.NewsComponent.dto.response.PaginationResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.exceptions.ResourceNotFoundException;
import com.example.NewsComponent.mapper.FileResponseMapper;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.helper.NewsQueryGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_HAS_INTEREST;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Repository
@AllArgsConstructor
public class NewsRepository {

    private final ArangoOperations arangoOperations;
    private final ArangoConverter arangoConverter;
    private final NewsRequestResponseMapper newsRequestResponseMapper;
    private final NewsQueryGenerator newsQueryGenerator;
    private final FileResponseMapper fileResponseMapper;


    public News getNewsById(String id) {
        String query = """
                FOR doc IN @@coll
                FILTER doc._key == @id
                RETURN doc
                """;
        Map<String, Object> queryFiller = Map.of(
                "@coll", NEWS,
                "id", id);

        ArangoCursor<News> cursor = arangoOperations.query(query,queryFiller, News.class);
        try (cursor) {
            Optional<News> first = cursor.stream().findFirst();
            if (first.isPresent()) {
                return first.get();
            } else {
                throw new ResourceNotFoundException("News with given id not found");
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }

    @SneakyThrows
    public Pagination<NewsResponse> getAllNews(NewsFilter newsFilter,
                                               PaginationFilter paginationFilter,
                                               NewsStatus newsStatus) {
        String finalQuery = newsQueryGenerator.getQuery(newsFilter, paginationFilter, newsStatus);
        System.out.println(finalQuery);
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
                throw new ResourceNotFoundException("No data found");
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public News saveNews(ArangoDatabase arangoDatabase,String transactionId, News news) {

        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(NEWS)
                .insertDocument(arangoConverter.write(news),
                        new DocumentCreateOptions()
                                .streamTransactionId(transactionId)
                                .returnNew(true));
        return arangoConverter.read(News.class, createEntity.getNew());
    }

    public News updateNews(ArangoDatabase arangoDatabase, String transactionId, News news) {
        DocumentUpdateEntity<VPackSlice> updatedNews = arangoDatabase.collection(NEWS)
                .updateDocument(news.getId(), arangoConverter.write(news),
                        new DocumentUpdateOptions()
                                .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(News.class, updatedNews.getNew());

    }

    // TODO: 08-06-2023 check the query /run
    public Pagination<NewsResponse> getNewsFromInterests(List<String> interestIds,
                                                         PaginationFilter paginationFilter){

        String query= """
                Let list = (
                FOR doc IN @@news
                FILTER doc.newsStatus == "PUBLISHED"
                    FOR v in 1..1
                    OUTBOUND doc
                    @@newsHasInterest
                    FILTER v._key in @ids
                    LIMIT @skip, @limit
                    SORT doc.publishedDate @order@
                    RETURN doc
                )
                LET total = (
                    FOR doc IN @@news
                    FILTER doc.newsStatus == "PUBLISHED"
                    FOR v in 1..1
                    OUTBOUND doc
                    @@newsHasInterest
                    FILTER v._key in @ids
                    COLLECT WITH COUNT INTO length
                    RETURN  length
                )
                """;

        Map<String,Object> template=new HashMap<>();
        template.put("@news",NEWS);
        template.put("ids", Jackson.toJsonString(interestIds));
        template.put("skip", paginationFilter.skip().toString());
        template.put("limit", paginationFilter.getLimit().toString());
        template.put("order", paginationFilter.getOrder().name());
        template.put("@newsHasInterest",NEWS_HAS_INTEREST);


        ArangoCursor<PaginationResponse> cursor =
                arangoOperations.query(query,template, PaginationResponse.class);
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
                throw new ResourceNotFoundException("No data found");
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

}
