package com.example.NewsComponent.repository.edge;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.edge.NewsHasComment;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Long countNoOfComments(String id) {
        String query = """
                FOR doc IN ${newsHasComment}
                FILTER doc._from==${id}
                COLLECT WITH COUNT INTO total
                return total
                """;

        Map<String, String> template = new HashMap<>();
        template.put("newsHasComment", NEWS_HAS_COMMENT);
        template.put("id", NEWS + "/" + id);

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String finalQuery = stringSubstitutor.replace(query);
        ArangoCursor<Long> cursor = arangoOperations.query(finalQuery, Long.class);

        try (cursor) {
            Optional<Long> optional = cursor.stream().findFirst();
            return optional.orElse(0L);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }
}
