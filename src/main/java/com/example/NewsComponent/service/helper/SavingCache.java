package com.example.NewsComponent.service.helper;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SavingCache {

    @Cacheable(value = "matcher1", key = "#text")
    public String searchPatternForSearchText(final String text){

        String excludePattern = "\\b(?:is|and|to|a|the|of|this|an|for|in|as|by|it)\\b";

        // Create a Pattern object and compile the regex pattern.
        Pattern pattern1 = Pattern.compile(excludePattern, Pattern.CASE_INSENSITIVE);

        // Create a Matcher object to find matches in the input string.
        Matcher matcher1 = pattern1.matcher(text);

        // Replace the matched words with an empty string and return the result.
        return matcher1.replaceAll("");
        //streamPublisher.publish(text, s);
//        System.out.println(s);
//        return s;

    }
}
