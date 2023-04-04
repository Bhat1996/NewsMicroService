package com.example.NewsComponent.domain.vertex;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import com.example.NewsComponent.metadata.VertexName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document(VertexName.NEWS_COMMENTS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsComments {
    @Id
    private String id;
    @ArangoId
    private String arangoId;
    private String text;
    private LocalDateTime createdDate;
}
