package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsLikedBy;
import org.springframework.stereotype.Repository;

@Repository
public class NewsLikedByRepository {
    private  final ArangoConverter arangoConverter;

    public NewsLikedByRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsLikedBy saveNewsLikedByEdge(ArangoDatabase arangoDatabase,
                                                           String transactionId,
                                                           NewsLikedBy newsLikedBy){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("newsLikedBy")
                .insertDocument(arangoConverter.write(newsLikedBy), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
      return   arangoConverter.read(NewsLikedBy.class,createEntity.getNew());

    }
}
