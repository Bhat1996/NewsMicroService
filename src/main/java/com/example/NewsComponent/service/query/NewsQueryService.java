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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.NewsComponent.metadata.VertexName.NEWS;
import static com.example.NewsComponent.repository.NewsRepository.*;


@Service
public class NewsQueryService {

    private final ArangoOperations arangoOperations;
    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsQueryService(ArangoOperations arangoOperations,
                            NewsRequestResponseMapper newsRequestResponseMapper) {
        this.arangoOperations = arangoOperations;
        this.newsRequestResponseMapper = newsRequestResponseMapper;

    }

    // TODO: 17-03-2023 add  for newsFilter
    public Pagination<NewsResponse> getAllNews(NewsStatus newsStatus,
                                               PaginationFilter paginationFilter,
                                               NewsFilter newsFilter) {

        String query = """
                LET list = (
                    FOR news IN ${news}
                    FILTER news.newsStatus == '${newsStatus}'
                    ${languageFilter}
                    ${countryIds}
                    ${stateIds}
                    ${districtIds}
                    ${tehsilIds}
                    ${villageIds}
                    ${status}
                    ${dateFilter}
                    SORT news.newsPublishDate ${order}
                    LIMIT ${skip}, ${limit}
                    RETURN news
                )
                LET total = (
                    FOR news IN news
                    FILTER news.newsStatus == '${newsStatus}'
                    ${languageFilter}
                    ${countryIds}
                    ${stateIds}
                    ${districtIds}
                    ${tehsilIds}
                    ${villageIds}
                    ${status}
                    ${dateFilter}
                    COLLECT WITH COUNT INTO size
                    RETURN size
                )
                                  
                RETURN {
                    list: list,
                    total: first(total)
                }
                """;


        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("news", NEWS);

        if (StringUtils.isNotBlank(newsFilter.getLanguage())) {
            queryParams.put("languageFilter", getLanguageFilter(newsFilter.getLanguage()));
        } else {
            queryParams.put("languageFilter", "");
        }

        if (newsFilter.getCountryIds() != null) {
            queryParams.put("countryIds", getCountryIds(newsFilter.getCountryIds()));
        } else {
            queryParams.put("countryIds", "");
        }

        if (newsFilter.getStateIds() != null) {
            queryParams.put("stateIds", getStateIds(newsFilter.getStateIds()));
        } else {
            queryParams.put("stateIds", "");
        }

        if (newsFilter.getDistrictIds() != null) {
            queryParams.put("districtIds", getDistrictIds(newsFilter.getDistrictIds()));
        } else {
            queryParams.put("districtIds", "");
        }

        if (newsFilter.getTehsilIds() != null) {
            queryParams.put("tehsilIds", getTehsilIds(newsFilter.getTehsilIds()));
        } else {
            queryParams.put("tehsilIds", "");
        }

        if (newsFilter.getVillageIds() != null) {
            queryParams.put("villageIds", getVillageIds(newsFilter.getVillageIds()));
        } else {
            queryParams.put("villageIds", "");
        }

        if(newsFilter.getStatus() !=null){
            queryParams.put("status",getStatusFilter(newsFilter.getStatus()));
        }else {
            queryParams.put("status","");
        }

        if (newsFilter.getDateFilter() !=null){
            queryParams.put("dateFilter",getDateFilter(newsFilter.getDateFilter()));
        }else {
            queryParams.put("dateFilter", "");
        }

        queryParams.put("order", paginationFilter.getOrder().toString());
        queryParams.put("skip", paginationFilter.skip().toString());
        queryParams.put("limit", paginationFilter.getLimit().toString());
        queryParams.put("newsStatus", newsStatus.toString());

        String finalQuery = new StringSubstitutor(queryParams).replace(query);
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
                        newsList.stream().map(newsRequestResponseMapper::getNewsResponse).toList();
                return new Pagination<>(responseList, pageInfo);
            } else {
                throw new RuntimeException("No data found");
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }


}
