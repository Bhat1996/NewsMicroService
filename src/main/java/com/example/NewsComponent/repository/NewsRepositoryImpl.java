package com.example.NewsComponent.repository;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.domain.News;
import org.springframework.stereotype.Repository;
import org.apache.commons.text.StringSubstitutor;




import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class NewsRepositoryImpl implements INewsRepository {
    private final ArangoOperations arangoOperations;

    public NewsRepositoryImpl(ArangoOperations arangoOperations) {
        this.arangoOperations = arangoOperations;
    }

    public News getNewsById(String id ){
        String query = """
                    for doc in ${coll}
                    filter doc._key == '${id}'
                    return doc
                    """;
        Map<String, String> queryFiller = Map.of(
                "coll", "NEWS",
                "id", id);
        String finalQuery = new StringSubstitutor(queryFiller).replace(query);
        ArangoCursor<News> cursor = arangoOperations.query(finalQuery, News.class);
        Optional<News> first = cursor.stream().findFirst();
        if(first.isPresent()){
            return first.get();
        }else {
            throw new RuntimeException("News with given id not found");
        }
    }


}
