package com.example.NewsComponent.dto.request;

import com.example.NewsComponent.enums.SearchType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Getter
@Setter
public class DiscoverNewsInput {
    @NotNull
    private SearchType searchType;
    Set<String> interestIds;

}
