package com.example.NewsComponent.repository.vertex;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.vertex.NewsComments;
import com.example.NewsComponent.exceptions.ResourceNotFoundException;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.NewsComponent.metadata.VertexName.NEWS_COMMENTS;

@Repository
public class NewsCommentsRepository {

    private  final ArangoConverter arangoConverter;
    private final ArangoOperations arangoOperations;

    public NewsCommentsRepository(ArangoConverter arangoConverter, ArangoOperations arangoOperations) {
        this.arangoConverter = arangoConverter;
        this.arangoOperations = arangoOperations;
    }

    public NewsComments saveNewsComments(ArangoDatabase arangoDatabase,
                                         String transactionId,
                                         NewsComments newsComments){
        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection(NEWS_COMMENTS)
                .insertDocument(arangoConverter.write(newsComments), new DocumentCreateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
        return  arangoConverter.read(NewsComments.class,createEntity.getNew());
    }

    public NewsComments getComment(String id){
        String query= """
                FOR doc IN ${NewsComments}
                FILTER doc._key=${id}
                RETURN doc
                """;
        Map<String,String> template=new HashMap<>();
        template.put("NewsComments",NEWS_COMMENTS);
        template.put("id",id);

        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        String finalQuery = stringSubstitutor.replace(query);

        ArangoCursor<NewsComments> cursor = arangoOperations.query(finalQuery, NewsComments.class);
        try (cursor){
            Optional<NewsComments> optional = cursor.stream().findFirst();
            if (optional.isPresent()){
                return optional.get();
            }else {
                throw new ResourceNotFoundException("news comment not found ");
            }
        }catch (IOException ioException){
            throw new RuntimeException(ioException);
        }
    }
}
