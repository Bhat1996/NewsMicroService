package com.example.NewsComponent.domain.edge;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Edge;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

import static com.example.NewsComponent.metadata.EdgeName.COMMENT_HAS_REPLY;

@Getter
@Setter
@Edge(COMMENT_HAS_REPLY)
public class CommentHasReply {
    @Id
    private String id;
    @ArangoId
    private String arangoId;
    private String _from;
    private String _to;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}
