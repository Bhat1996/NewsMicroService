package com.example.NewsComponent.dto.request;

import com.example.NewsComponent.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NewsFilter {

    private String language;
    private String searchIt;
    private Set<String> countryIds;
    private Set<String> stateIds;
    private Set<String> districtIds;
    private Set<String> tehsilIds;
    private Set<String> villageIds;
    private Status status;
    private DateFilter dateFilter;
}
