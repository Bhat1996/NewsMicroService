package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasHashTag;
import org.springframework.stereotype.Repository;

@Repository
public class NewsHasHashTagRepository {
    private final ArangoConverter arangoConverter;

    public NewsHasHashTagRepository(ArangoConverter arangoConverter) {
        this.arangoConverter = arangoConverter;
    }

    public NewsHasHashTag saveNewsHasHashtagsEdge(ArangoDatabase arangoDatabase,
                                                  String transactionId,
                                                  NewsHasHashTag newsHasHashTag) {
        DocumentCreateEntity<VPackSlice> documentCreateEntity = arangoDatabase.collection("newsHasHashTag").
                insertDocument(arangoConverter.write(newsHasHashTag),
                        new DocumentCreateOptions().streamTransactionId(transactionId).returnNew(true));
        VPackSlice aNew = documentCreateEntity.getNew();
        return arangoConverter.read(NewsHasHashTag.class, aNew);
    }
}
