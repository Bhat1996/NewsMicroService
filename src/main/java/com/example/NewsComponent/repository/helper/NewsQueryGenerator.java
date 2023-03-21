package com.example.NewsComponent.repository.helper;

import com.example.NewsComponent.dto.request.DateFilter;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Service
public class NewsQueryGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getAllNews(NewsFilter newsFilter, PaginationFilter paginationFilter, NewsStatus newsStatus) {
// TODO: 21-03-2023 add search filter
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

        return new StringSubstitutor(queryParams).replace(query);
    }


    public static String getLanguageFilter(String value) {
        String query = "filter news.title.en == ${value}";
        Map<String, String> template = Map.of("value", value);
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getCountryIds(Set<String> countryIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(countryIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getStateIds(Set<String> stateIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(stateIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getDistrictIds(Set<String> districtIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(districtIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getTehsilIds(Set<String> tehsilIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(tehsilIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getVillageIds(Set<String> villageIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(villageIds) );
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    public static String getStatusFilter(Status status) {
        String query = "filter news.status== '${status}'";
        Map<String, String> template = Map.of("status", status.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    public static String getDateFilter(DateFilter dateFilter) {
        if (dateFilter.getStartDate() != null && dateFilter.getEndDate() != null) {
            String query = "filter news.newsPublishDate >= ${startDate} And news.newsPublishDate<= ${endDate}";
            Map<String, String> template = Map.of("startDate", dateFilter.getStartDate(),
                    "endDate", dateFilter.getEndDate());
            StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
            return stringSubstitutor.replace(query);
        } else {
            return "";
        }
    }
}
