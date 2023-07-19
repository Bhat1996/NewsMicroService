package com.example.NewsComponent.service.helper;

import com.example.NewsComponent.dto.vertex.News;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SearchTextService {
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

            Pattern p=Pattern.compile("\s");
            Matcher matcher=p.matcher("[s]");
            String[] s=p.split("en");
            engData.add(title.get("en"));
            System.out.println(engData);
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

    public String searchPatternForSearchText(String regex, String text){
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(text);
//        int matches = 0;
//        while (matcher.find()) {
//            matches++;
//        }
//        return matches;

        String excludePattern = "\\b(?:is|and|to|a|the|of|this|an)\\b";

        // Create a Pattern object and compile the regex pattern.
        Pattern pattern1 = Pattern.compile(excludePattern, Pattern.CASE_INSENSITIVE);

        // Create a Matcher object to find matches in the input string.
        Matcher matcher1 = pattern1.matcher(text);

        // Replace the matched words with an empty string and return the result.
        return matcher1.replaceAll("");

    }

}
