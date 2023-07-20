package com.example.NewsComponent.service.helper;

import com.example.NewsComponent.dto.vertex.News;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchTextService {
    private final ObjectMapper objectMapper;
    private final StreamPublisher streamPublisher;

    public SearchTextService(ObjectMapper objectMapper,
                             StreamPublisher streamPublisher) {
        this.objectMapper = objectMapper;
        this.streamPublisher = streamPublisher;
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
            String filteredEnglishData = searchPatternForSearchText(text.get("en"));
            engData.add(filteredEnglishData);
        }

        boolean hindiTitleNotBlank = StringUtils.isNotBlank(text.get("hn"));
        if (hindiTitleNotBlank) {
            String filteredHindiData = searchPatternForSearchText(text.get("hn"));
            hindiData.add(filteredHindiData);
        }

        boolean punjabiTitleNotBlank = StringUtils.isNotBlank(text.get("pb"));
        if (punjabiTitleNotBlank) {
            String filteredPunjabiData = searchPatternForSearchText(text.get("pb"));
            punjabiData.add(filteredPunjabiData);
        }

    }


    @CachePut(value = "searchTextCache", key = "#text")
    public String searchPatternForSearchText(final String text){

        String excludePattern = "\\b(?:is|and|to|a|the|of|this|an|for|in|as|by|it)\\b";

        // Create a Pattern object and compile the regex pattern.
        Pattern pattern1 = Pattern.compile(excludePattern, Pattern.CASE_INSENSITIVE);

        // Create a Matcher object to find matches in the input string.
        Matcher matcher1 = pattern1.matcher(text);

        // Replace the matched words with an empty string and return the result.
        String s = matcher1.replaceAll("");
        streamPublisher.publish(text, s);
        System.out.println(s);
        return s;

    }

}
