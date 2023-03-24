package com.example.NewsComponent.dto.response;

import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.SortingOrder;
import com.example.NewsComponent.enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service
@Getter
@Setter
public class NewsResponse {
        private List<FileUrl> imageUrls;
        private List<FileUrl> audioUrls;
        private List<FileUrl> videoUrls;
        private List<FileUrl> documentUrls;
        private String id;
        private String arangoId;
        private Map<String,String> title;
        private Map<String,String> description;
        private Map<String,String>  slugTitle;
        private List<String> hashTagIds;
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
