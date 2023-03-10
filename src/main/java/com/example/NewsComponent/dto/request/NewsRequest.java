package com.example.NewsComponent.dto.request;

import com.example.NewsComponent.domain.embedded.LanguageSupport;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Getter
@Setter
public class NewsRequest {

    @Id
    private String id;
    @NotNull
    private LanguageSupport title;
    private LanguageSupport description;
    private LanguageSupport slugTitle;
    private List<String> hashTagIds;
    private boolean publishAndNotify = false;
    private NewsStatus newsStatus;
    private Status status;
    @NotBlank
    private String remarks;
    @NotNull
    private List<String> interestIds;
    @NotBlank
    private String newsSource;
    @NotBlank
    private String sourceLink;
    private LocalDateTime newsPublishDate;
    @NotNull
    private Set<String> countryIds;
    private Set<String> stateIds;
    private Set<String> districtIds;
    private Set<String> tehsilIds;
    private Set<String> villageIds;
}