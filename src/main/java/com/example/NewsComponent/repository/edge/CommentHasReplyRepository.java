package com.example.NewsComponent.repository.edge;

import com.arangodb.springframework.core.convert.ArangoConverter;
import org.springframework.stereotype.Repository;

@Repository
public class CommentHasReplyRepository {
    private  final ArangoConverter arangoConverter;

    public CommentHasReplyRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }


}
