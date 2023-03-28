package com.example.NewsComponent.dto.internal;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SearchTokenHelper {
    private static final int MINIMUM_TOKEN_LENGTH = 2;
    private final String[] splitSearchWords;
    public SearchTokenHelper(final String searchWord) {
        this.splitSearchWords = StringUtils.split(searchWord);
    }

    public boolean isThereAnySearchableTokenWord() {
        return Arrays.stream(splitSearchWords)
                .anyMatch(s -> s.length() >= MINIMUM_TOKEN_LENGTH);
    }

    public String getOnlySearchableTokenWord() {
        List<String> words = Arrays.stream(splitSearchWords)
                .filter(s -> s.length() >= MINIMUM_TOKEN_LENGTH)
                .toList();
        return String.join(" ", words);
    }

}