package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsIsForLocation;
import org.springframework.stereotype.Repository;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_IS_FOR_LOCATION;

@Repository
public class NewsIsForLocationRepository {

    private final ArangoConverter arangoConverter;

    public NewsIsForLocationRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsIsForLocation saveNewsForLocation(ArangoDatabase arangoDatabase,
                                             String transactionId,
                                             NewsIsForLocation newsIsForLocation){
        DocumentCreateEntity<VPackSlice> documentCreateEntity = arangoDatabase.collection(NEWS_IS_FOR_LOCATION).
                insertDocument(arangoConverter.write(newsIsForLocation),
                        new DocumentCreateOptions().streamTransactionId(transactionId).returnNew(true));
        VPackSlice aNew = documentCreateEntity.getNew();
        return arangoConverter.read(NewsIsForLocation.class,aNew);
    }
}
