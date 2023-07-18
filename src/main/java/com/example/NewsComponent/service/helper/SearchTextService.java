package com.example.NewsComponent.service.helper;

import com.example.NewsComponent.dto.vertex.News;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchTextService {
    // TODO: 16-07-2023 while adding news take title and desc. for searchText.
    //if searchText is used make a webClient call to other service.

    //other way is this: loop on news
    private final ObjectMapper objectMapper;

    public SearchTextService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public void createSearchText(News news) {
        Set<String> engData = new HashSet<>();
        Set<String> hindiData = new HashSet<>();
        Set<String> punjabiData = new HashSet<>();

        addTitle(news, engData, punjabiData, hindiData);
        addDescription(news, engData, punjabiData, hindiData);

        String en = String.join(" ", engData);
        String hn = String.join(" ", hindiData);
        String pb = String.join(" ", punjabiData);

        Map<String, String> allLanguage = new HashMap<>();
        allLanguage.put("en", en);
        allLanguage.put("hn", hn);
        allLanguage.put("pb", pb);


        news.setSearchText(allLanguage);

    }

    private void addDescription(News news, Set<String>  engData, Set<String>  punjabiData,
                                Set<String> hindiData) {
        Map<String, String> description = news.getDescription();
        addToList(description, engData, hindiData, punjabiData);
    }

    private void addTitle(News news, Set<String>  engData, Set<String>  punjabiData,
                          Set<String>  hindiData) {

        Map<String, String> title = news.getTitle();
        addToList(title, engData, hindiData, punjabiData);
    }

    private void addToList(Map<String, String> title, Set<String>  engData,
                           Set<String> hindiData, Set<String>  punjabiData) {


        boolean englishTitleNotBlank = StringUtils.isNotBlank(title.get("en"));
        if (englishTitleNotBlank) {
            engData.add(title.get("en"));
        }

        boolean hindiTitleNotBlank = StringUtils.isNotBlank(title.get("hn"));
        if (hindiTitleNotBlank) {
            hindiData.add(title.get("hn"));
        }

        boolean punjabiTitleNotBlank = StringUtils.isNotBlank(title.get("pb"));
        if (punjabiTitleNotBlank) {
            punjabiData.add(title.get("pb"));
        }

    }

}
