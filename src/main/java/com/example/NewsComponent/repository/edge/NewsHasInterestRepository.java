package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasInterest;
import org.springframework.stereotype.Repository;

@Repository
public class NewsHasInterestRepository {

    private final ArangoConverter arangoConverter;

    public NewsHasInterestRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsHasInterest saveNewsHasInterestEdge(ArangoDatabase arangoDatabase,
                                                   String transactionId,
                                                   NewsHasInterest newsHasInterest){
        DocumentCreateEntity<VPackSlice> documentCreateEntity = arangoDatabase.collection("newsHasInterest")
                .insertDocument(arangoConverter.write(newsHasInterest), new DocumentCreateOptions()
                        .streamTransactionId(transactionId)
                        .returnNew(true));
        VPackSlice aNew = documentCreateEntity.getNew();
        return arangoConverter.read(NewsHasInterest.class, aNew);

    }
}
