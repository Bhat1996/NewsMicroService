package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsSharedBy;
import org.springframework.stereotype.Repository;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_SHARED_BY;

@Repository
public class NewsSharedByRepository {
    private final ArangoConverter arangoConverter;

    public NewsSharedByRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsSharedBy saveNewsSharedByEdge(ArangoDatabase arangoDatabase,
                                             String transactionId,
                                             NewsSharedBy newsSharedBy) {
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(NEWS_SHARED_BY)
                .insertDocument(arangoConverter.write(newsSharedBy), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));

        return arangoConverter.read(NewsSharedBy.class, createEntity.getNew());
    }
}
