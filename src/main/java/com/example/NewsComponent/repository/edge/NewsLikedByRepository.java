package com.example.NewsComponent.repository.edge;

import com.amazonaws.util.json.Jackson;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsLikedBy;
import com.example.NewsComponent.dto.internal.LikesDto;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

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

    // TODO: 05-04-2023 use it and check
    public List<LikesDto> countNumberOfLikes(Set<String> ids){
        String query= """
                FOR doc IN ${NEWS}
                FILTER doc._key IN ${ids}
                   LET value = (
                        FOR v in 1..1
                        OUTBOUND doc
                       ${newsLikedBy}
                        return true
                   )
                   
                   RETURN {
                        id: doc._key,
                        total: length(value)
                   }
                """;
        Map<String,String> template=new HashMap<>();
        template.put("newsLikedBy", NEWS_LIKED_BY);
        template.put("NEWS", NEWS);
        template.put("ids", Jackson.toJsonString(ids));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String finalQuery = stringSubstitutor.replace(query);
        ArangoCursor<LikesDto> cursor = arangoOperations.query(finalQuery, LikesDto.class);

        try(cursor){
            return cursor.asListRemaining();
        }catch (IOException ioException){
            throw new RuntimeException(ioException);
        }

    }
}
