package com.example.NewsComponent.dto;

import com.arangodb.springframework.annotation.ArangoId;
import com.example.NewsComponent.domain.embedded.LanguageSupport;
import com.example.NewsComponent.enums.NewsStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Getter
@Setter
public class NewsDto {
    @Id
    String id;
    @ArangoId
    String arangoId;
    private boolean publishAndNotify = false;
    private LanguageSupport title;
    private LanguageSupport description;
    private LanguageSupport slugTitle;
    private List<String> hashTagIds;
    private String remarks;
    private List<String> interestIds;
    private String newsSource;
    private String sourceLink;
    private LocalDateTime newsPublishDate;
    private Set<String> countryIds;
    private Set<String> stateIds;
    private Set<String> districtIds;
    private Set<String> tehsilIds;
    private Set<String> villageIds;
    private NewsStatus newsStatus = NewsStatus.DRAFT;
}
