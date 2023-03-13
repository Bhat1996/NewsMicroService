package com.example.NewsComponent.repository;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.convert.ArangoConverter;
import com.arangodb.velocypack.VPackSlice;
import com.example.NewsComponent.domain.News;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class NewsRepository {

    private final ArangoOperations arangoOperations;
    private final ArangoConverter arangoConverter;

    public NewsRepository(ArangoOperations arangoOperations, ArangoConverter arangoConverter) {
        this.arangoOperations = arangoOperations;
        this.arangoConverter = arangoConverter;
    }


    public News getNewsById(String id) {
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
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new RuntimeException("News with given id not found");
        }
    }

    public News saveNews(ArangoDatabase arangoDatabase, String transactionId, News news) {

        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("NEWS")
                .insertDocument(arangoConverter.write(news),
                        new DocumentCreateOptions()
                                .streamTransactionId(transactionId)
                                .returnNew(true));

        VPackSlice aNew = createEntity.getNew();
        return arangoConverter.read(News.class, aNew);
    }

    public News updateNews(ArangoDatabase arangoDatabase, String transactionId, News news) {
        DocumentUpdateEntity<VPackSlice> updatedNews = arangoDatabase.collection("NEWS")
                .updateDocument(news.getId(), arangoConverter.write(news),
                new DocumentUpdateOptions()
                        .streamTransactionId(transactionId).returnNew(true));
        VPackSlice aNew = updatedNews.getNew();
        return arangoConverter.read(News.class,aNew);

    }

}
