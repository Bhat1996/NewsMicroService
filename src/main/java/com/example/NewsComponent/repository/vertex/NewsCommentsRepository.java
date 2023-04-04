package com.example.NewsComponent.repository.vertex;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.vertex.NewsComments;
import org.springframework.stereotype.Repository;

@Repository
public class NewsCommentsRepository {

    private  final ArangoConverter arangoConverter;

    public NewsCommentsRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsComments saveNewsComments(ArangoDatabase arangoDatabase,
                                         String transactionId,
                                         NewsComments newsComments){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("newsComments")
                .insertDocument(arangoConverter.write(newsComments), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
        return  arangoConverter.read(NewsComments.class,createEntity.getNew());
    }
}
