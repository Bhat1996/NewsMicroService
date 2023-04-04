package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasComment;
import org.springframework.stereotype.Repository;

@Repository
public class NewsHasCommentRepository {

    private  final ArangoConverter arangoConverter;

    public NewsHasCommentRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsHasComment saveNewsHasCommentEdge(ArangoDatabase arangoDatabase,
                                                 String transactionId,
                                                 NewsHasComment newsHasComment){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("newsHasComment")
                .insertDocument(arangoConverter.write(newsHasComment), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
       return  arangoConverter.read(NewsHasComment.class,createEntity.getNew());
    }
}
