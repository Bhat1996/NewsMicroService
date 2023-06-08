package com.example.NewsComponent.validations;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.dto.request.NewsRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.NewsComponent.utils.Not.not;
@Service
public class LanguageValidator {

    private  final ArangoOperations arangoOperations;

    public LanguageValidator(ArangoOperations arangoOperations) {
        this.arangoOperations = arangoOperations;
    }
    public void languageValidateChecker(NewsRequest newsRequest){
        throwExceptionIfLanguageIsNotValid(newsRequest.getTitle());
        throwExceptionIfLanguageIsNotValid(newsRequest.getDescription());
        throwExceptionIfLanguageIsNotValid(newsRequest.getSlugTitle());
    }

    public void throwExceptionIfLanguageIsNotValid(Map<String, String> name){
        Set<String> inValidLanguages  = getInValidLanguageForCreateOrUpdate(name);
        if (inValidLanguages.size() > 0) {
            throw new IllegalArgumentException("You can't pass " + inValidLanguages + " on creation");
        }

        String valueInHindi = name.get("hn");
        if(StringUtils.isBlank(valueInHindi)) {
            throw new IllegalArgumentException("Hindi is mandatory");
        }
    }

    private Set<String> getInValidLanguageForCreateOrUpdate(Map<String, String> name) {
        String query= """
                for language in languages
                filter language.status != "DELETED"
                return language.code
                """;
        ArangoCursor<String> cursor = arangoOperations.query(query, String.class);
        try(cursor){
            Set<String> languagesInDatabase = cursor.stream().collect(Collectors.toSet());
            Set<String> languagesFromUi = name.keySet();

            return languagesFromUi
                            .stream()
                            .filter(language -> not(languagesInDatabase.contains(language)))
                            .collect(Collectors.toSet());
        }catch (IOException ioException){
            throw new RuntimeException(ioException);
        }
    }

}

