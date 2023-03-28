package com.example.NewsComponent.domain.edge;

import com.example.NewsComponent.metadata.EdgeName;
import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Edge;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Edge(EdgeName.NEWS_IS_FOR_LOCATION)
@Getter
@Setter
public class NewsIsForLocation {
    @Id
    private String id;
    @ArangoId
    private String arangoId;
    private String _from;
    private String _to;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
