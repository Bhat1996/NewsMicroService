package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.CommentHasReply;
import com.example.NewsComponent.domain.edge.ReplyGivenByUser;
import org.springframework.stereotype.Repository;

import static com.example.NewsComponent.metadata.EdgeName.COMMENT_HAS_REPLY;
import static com.example.NewsComponent.metadata.EdgeName.REPLY_GIVEN_BY_USER;

@Repository
public class CommentHasReplyRepository {
    private  final ArangoConverter arangoConverter;

    public CommentHasReplyRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }
    public CommentHasReply saveCommentHasReplyEdge(ArangoDatabase arangoDatabase,
                                                   String transactionId,
                                                   CommentHasReply commentHasReply){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(COMMENT_HAS_REPLY)
                .insertDocument(arangoConverter.write(commentHasReply), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
       return arangoConverter.read(CommentHasReply.class,createEntity.getNew());
    }

    public ReplyGivenByUser saveReplyGivenByUser(ArangoDatabase arangoDatabase,
                                                 String transactionId,
                                                 ReplyGivenByUser replyGivenByUser){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(REPLY_GIVEN_BY_USER)
                .insertDocument(arangoConverter.write(replyGivenByUser), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(ReplyGivenByUser.class,createEntity.getNew());
    }
}
