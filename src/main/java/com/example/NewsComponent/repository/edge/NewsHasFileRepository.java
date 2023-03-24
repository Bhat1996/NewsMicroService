package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasFile;
import org.springframework.stereotype.Repository;

@Repository
public class NewsHasFileRepository {

    private  final ArangoConverter arangoConverter;

    public NewsHasFileRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsHasFile saveNewsHasFileEdge(ArangoDatabase arangoDatabase,
                                           String transactionId,
                                           NewsHasFile newsHasFile){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("newsHasFile")
                .insertDocument(arangoConverter.write(newsHasFile),
                new DocumentCreateOptions().streamTransactionId(transactionId).returnNew(true));

        return  arangoConverter.read(NewsHasFile.class,createEntity.getNew());
    }
}
