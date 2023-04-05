package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.CommentHasReply;
import org.springframework.stereotype.Repository;

@Repository
public class CommentHasReplyRepository {
    private  final ArangoConverter arangoConverter;

    public CommentHasReplyRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }
    public CommentHasReply saveCommentHasReplyEdge(ArangoDatabase arangoDatabase,
                                                   String transactionId,
                                                   CommentHasReply commentHasReply){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("commentHasReply")
                .insertDocument(arangoConverter.write(commentHasReply), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
       return arangoConverter.read(CommentHasReply.class,createEntity.getNew());
    }
}
