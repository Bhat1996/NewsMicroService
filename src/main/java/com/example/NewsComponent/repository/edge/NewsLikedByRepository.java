package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsLikedBy;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_LIKED_BY;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Repository
public class NewsLikedByRepository {
    private  final ArangoConverter arangoConverter;
    private  final ArangoOperations arangoOperations;

    public NewsLikedByRepository(ArangoConverter arangoConverter, ArangoOperations arangoOperations) {
        this.arangoConverter = arangoConverter;
        this.arangoOperations = arangoOperations;
    }

    public NewsLikedBy saveNewsLikedByEdge(ArangoDatabase arangoDatabase,
                                                           String transactionId,
                                                           NewsLikedBy newsLikedBy){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("newsLikedBy")
                .insertDocument(arangoConverter.write(newsLikedBy), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
      return   arangoConverter.read(NewsLikedBy.class,createEntity.getNew());

    }

    // TODO: 05-04-2023 use it
    public Long countNumberOfLikes(String id){
        String query= """
                FOR doc IN ${newsLikedBy}
                FILTER doc._from==${id}
                COLLECT WITH COUNT INTO total
                return total
                """;
        Map<String,String> template=new HashMap<>();
        template.put("newsLikedBy",NEWS_LIKED_BY);
        template.put("id", NEWS+"/"+id);

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String finalQuery = stringSubstitutor.replace(query);
        ArangoCursor<Long> cursor = arangoOperations.query(finalQuery, Long.class);

        try(cursor){
            Optional<Long> optional = cursor.stream().findFirst();
            return optional.orElse(0L);
        }catch (IOException ioException){
            throw new RuntimeException(ioException);
        }

    }
}
