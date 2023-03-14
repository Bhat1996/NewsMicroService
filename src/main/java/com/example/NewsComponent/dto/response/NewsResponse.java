package com.example.NewsComponent.dto.response;

import com.example.NewsComponent.domain.embedded.LanguageSupport;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.SortingOrder;
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
public class NewsResponse {

        private String id;
        private String arangoId;
        private LanguageSupport title;
        private LanguageSupport description;
        private LanguageSupport slugTitle;
        private List<String> hashTagIds;
        private boolean publishAndNotify = false;
        private NewsStatus newsStatus;
        private Status status;
        private SortingOrder sortingOrder;
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
}
