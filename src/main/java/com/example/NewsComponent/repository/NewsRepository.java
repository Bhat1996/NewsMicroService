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
import com.example.NewsComponent.dto.request.DateFilter;
import com.example.NewsComponent.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class NewsRepository {

    private final ArangoOperations arangoOperations;
    private final ArangoConverter arangoConverter;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public NewsRepository(ArangoOperations arangoOperations,
                          ArangoConverter arangoConverter) {
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
                "coll", "news",
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

        DocumentCreateEntity<VPackSlice> createEntity = arangoDatabase.collection("news")
                .insertDocument(arangoConverter.write(news),
                        new DocumentCreateOptions()
                                .streamTransactionId(transactionId)
                                .returnNew(true));

        VPackSlice aNew = createEntity.getNew();
        return arangoConverter.read(News.class, aNew);
    }

    public News updateNews(ArangoDatabase arangoDatabase, String transactionId, News news) {
        DocumentUpdateEntity<VPackSlice> updatedNews = arangoDatabase.collection("news")
                .updateDocument(news.getId(), arangoConverter.write(news),
                        new DocumentUpdateOptions()
                                .streamTransactionId(transactionId).returnNew(true));
        VPackSlice aNew = updatedNews.getNew();
        return arangoConverter.read(News.class, aNew);

    }

    public static String getLanguageFilter(String value) {
        String query = "filter news.title.en == ${value}";
        Map<String, String> template = Map.of("value", value);
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getCountryIds(Set<String> countryIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(countryIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getStateIds(Set<String> stateIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(stateIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getDistrictIds(Set<String> districtIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(districtIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getTehsilIds(Set<String> tehsilIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(tehsilIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }
    @SneakyThrows
    public static String getVillageIds(Set<String> villageIds) {
        String query = "filter news.countryIds== ${value}";
        Map<String, String> template = Map.of("value", objectMapper.writeValueAsString(villageIds) );
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    public static String getStatusFilter(Status status) {
        String query = "filter news.status== '${status}'";
        Map<String, String> template = Map.of("status", status.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    public static String getDateFilter(DateFilter dateFilter) {
        if (dateFilter.getStartDate() != null && dateFilter.getEndDate() != null) {
            String query = "filter news.newsPublishDate >= ${startDate} And news.newsPublishDate<= ${endDate}";
            Map<String, String> template = Map.of("startDate", dateFilter.getStartDate(),
                    "endDate", dateFilter.getEndDate());
            StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
            return stringSubstitutor.replace(query);
        } else {
            return "";
        }
    }

}
