package com.example.NewsComponent.domain;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.SortingOrder;
import com.example.NewsComponent.enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Document("news")
public class News {

    @Id
    String id;
    @ArangoId
    String arangoId;
    private Map<String,String> title;
    private Map<String,String> description;
    private Map<String,String>  slugTitle;
    private List<String> hashTagIds;
    private NewsStatus newsStatus;
    private SortingOrder sortingOrder;
    private Status status;
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
