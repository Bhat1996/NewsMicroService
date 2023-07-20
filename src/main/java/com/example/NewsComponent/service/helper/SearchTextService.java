package com.example.NewsComponent.service.helper;

import com.example.NewsComponent.dto.vertex.News;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SearchTextService {
    private final ObjectMapper objectMapper;
    private final StreamPublisher streamPublisher;

    private  final SavingCache savingCache;

    public SearchTextService(ObjectMapper objectMapper,
                             StreamPublisher streamPublisher, SavingCache savingCache) {
        this.objectMapper = objectMapper;
        this.streamPublisher = streamPublisher;
        this.savingCache = savingCache;
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
        addDataToList(description, engData, hindiData, punjabiData);
    }

    private void addTitle(News news, Set<String>  engData, Set<String>  punjabiData,
                          Set<String>  hindiData) {

        Map<String, String> title = news.getTitle();
        addDataToList(title, engData, hindiData, punjabiData);
    }

    private void addDataToList(Map<String, String> text, Set<String>  engData,
                               Set<String> hindiData, Set<String>  punjabiData) {


        boolean englishTitleNotBlank = StringUtils.isNotBlank(text.get("en"));
        if (englishTitleNotBlank) {
            String filteredEnglishData = savingCache.searchPatternForSearchText(text.get("en"));
            engData.add(filteredEnglishData);
        }

        boolean hindiTitleNotBlank = StringUtils.isNotBlank(text.get("hn"));
        if (hindiTitleNotBlank) {
            String filteredHindiData = savingCache.searchPatternForSearchText(text.get("hn"));
            hindiData.add(filteredHindiData);
        }

        boolean punjabiTitleNotBlank = StringUtils.isNotBlank(text.get("pb"));
        if (punjabiTitleNotBlank) {
            String filteredPunjabiData = savingCache.searchPatternForSearchText(text.get("pb"));
            punjabiData.add(filteredPunjabiData);
        }

    }




}
