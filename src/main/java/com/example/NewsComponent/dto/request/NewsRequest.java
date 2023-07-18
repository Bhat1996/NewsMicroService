package com.example.NewsComponent.dto.request;

import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.SortingOrder;
import com.example.NewsComponent.enums.Status;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class NewsRequest {

    private String id;
    @NotNull(message = "provide valid title")
    private Map<String,String> title;
    private Map<String,String>  description;
    private Map<String,String>  slugTitle;
    private List<String> hashTagIds;
    @NotNull
    private NewsStatus newsStatus;
    private Status status;
    private SortingOrder sortingOrder;
    @NotBlank
    private String remarks;
    @NotNull(message = "provide valid interest")
    private List<String> interestIds;
    @NotBlank(message = "provide valid newsSource")
    private String newsSource;
    @NotBlank(message = "provide valid sourceLink")
    private String sourceLink;
    private Map<String,String> searchText;
    private LocalDateTime newsPublishDate;
    @NotNull
    private Set<String> countryIds;
    private Set<String> stateIds;
    private Set<String> districtIds;
    private Set<String> tehsilIds;
    private Set<String> villageIds;
    @NotNull(message = "provide fileInput")
    private FileInput fileInput;
}