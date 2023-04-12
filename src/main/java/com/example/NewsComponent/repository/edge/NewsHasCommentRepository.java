package com.example.NewsComponent.repository.edge;

import com.amazonaws.util.json.Jackson;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasComment;
import com.example.NewsComponent.dto.internal.ResultDto;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_HAS_COMMENT;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Repository
public class NewsHasCommentRepository {

    private final ArangoConverter arangoConverter;

    private final ArangoOperations arangoOperations;

    public NewsHasCommentRepository(ArangoConverter arangoConverter, ArangoOperations arangoOperations) {
        this.arangoConverter = arangoConverter;
        this.arangoOperations = arangoOperations;
    }

    public NewsHasComment saveNewsHasCommentEdge(ArangoDatabase arangoDatabase,
                                                 String transactionId,
                                                 NewsHasComment newsHasComment) {
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(NEWS_HAS_COMMENT)
                .insertDocument(arangoConverter.write(newsHasComment), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
        return arangoConverter.read(NewsHasComment.class, createEntity.getNew());
    }

    // TODO: 06-04-2023 use it
    public List<ResultDto> countNoOfComments(Set<String> ids) {
        String query = """
              FOR doc IN ${news}
                FILTER doc._key IN ${ids}
                   LET value = (
                        FOR v in 1..1
                        OUTBOUND doc
                        ${newsHasComment}
                        return true
                   )
                   
                   RETURN {
                        id: doc._key,
                        total: length(value)
                   }
                """;

        Map<String, String> template = new HashMap<>();
        template.put("newsHasComment", NEWS_HAS_COMMENT);
        template.put("news",NEWS);
        template.put("ids", Jackson.toJsonString(ids));

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String finalQuery = stringSubstitutor.replace(query);
        ArangoCursor<ResultDto> cursor = arangoOperations.query(finalQuery, ResultDto.class);

        try (cursor) {
            return cursor.asListRemaining();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }
}
