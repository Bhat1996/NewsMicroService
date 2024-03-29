package com.example.NewsComponent.repository.helper;

import com.amazonaws.util.json.Jackson;
import com.arangodb.ArangoCursor;
import com.arangodb.springframework.core.ArangoOperations;
import com.example.NewsComponent.dto.internal.SearchTokenHelper;
import com.example.NewsComponent.dto.request.DateFilter;
import com.example.NewsComponent.dto.request.NewsFilter;
import com.example.NewsComponent.dto.request.PaginationFilter;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.NewsComponent.metadata.VertexName.NEWS;
import static com.example.NewsComponent.metadata.ViewName.NEWS_SEARCH;
import static com.example.NewsComponent.utils.Not.not;

@Service
public class NewsQueryGenerator {

    private final ArangoOperations arangoOperations;

    public NewsQueryGenerator(ArangoOperations arangoOperations) {
        this.arangoOperations = arangoOperations;
    }


    public String getQuery(NewsFilter newsFilter, PaginationFilter paginationFilter, NewsStatus newsStatus) {

        String query = """             
                LET total = (
                    ${uniqueSortedListBasedOnScore}
                    FILTER doc.newsStatus == '${newsStatus}'
                    ${languageFilter}
                    ${countryIds}
                    ${stateIds}
                    ${districtIds}
                    ${tehsilIds}
                    ${villageIds}
                    ${status}
                    ${dateFilter}
                    COLLECT WITH COUNT INTO size
                    RETURN size
                )
                LET finalCount=(
                    ${uniqueSortedListBasedOnScore}
                    FILTER doc.newsStatus == '${newsStatus}'
                    SORT doc.newsPublishDate ${order}
                    ${languageFilter}
                    ${countryIds}
                    ${stateIds}
                    ${districtIds}
                    ${tehsilIds}
                    ${villageIds}
                    ${status}
                    ${dateFilter}
                    LIMIT ${skip}, ${limit}
                    RETURN doc
                )
                RETURN {
                    list:finalCount,
                    total: first(total)
                }
                """;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("news", NEWS);
        queryParams.put("uniqueSortedListBasedOnScore", getUniqueSortedListBasedOnScore(newsFilter.getSearchIt()));

        // TODO: 18-04-2023 look for other options or design pattern for multiple if-else
//        if (StringUtils.isNotBlank(newsFilter.getLanguage())) {
//            queryParams.put("languageFilter", getLanguageFilter(newsFilter.getLanguage()));
//        } else {
//            queryParams.put("languageFilter", "");
//        }
//
//        if (newsFilter.getCountryIds() != null) {
//            queryParams.put("countryIds", getCountryIds(newsFilter.getCountryIds()));
//        } else {
//            queryParams.put("countryIds", "");
//        }
//
//        if (newsFilter.getStateIds() != null) {
//            queryParams.put("stateIds", getStateIds(newsFilter.getStateIds()));
//        } else {
//            queryParams.put("stateIds", "");
//        }
//
//        if (newsFilter.getDistrictIds() != null) {
//            queryParams.put("districtIds", getDistrictIds(newsFilter.getDistrictIds()));
//        } else {
//            queryParams.put("districtIds", "");
//        }
//
//        if (newsFilter.getTehsilIds() != null) {
//            queryParams.put("tehsilIds", getTehsilIds(newsFilter.getTehsilIds()));
//        } else {
//            queryParams.put("tehsilIds", "");
//        }
//
//        if (newsFilter.getVillageIds() != null) {
//            queryParams.put("villageIds", getVillageIds(newsFilter.getVillageIds()));
//        } else {
//            queryParams.put("villageIds", "");
//        }
//
//        if (newsFilter.getStatus() != null) {
//            queryParams.put("status", getStatusFilter(newsFilter.getStatus()));
//        } else {
//            queryParams.put("status", "");
//        }
//
//        if (newsFilter.getDateFilter() != null) {
//            queryParams.put("dateFilter", getDateFilter(newsFilter.getDateFilter()));
//        } else {
//            queryParams.put("dateFilter", "");
//        }

        queryParams.put("languageFilter", StringUtils.isNotBlank(newsFilter.getLanguage()) ? getLanguageFilter(newsFilter.getLanguage()) : "");
        queryParams.put("countryIds", newsFilter.getCountryIds() != null ? getCountryIds(newsFilter.getCountryIds()) : "");
        queryParams.put("stateIds", newsFilter.getStateIds() != null ? getStateIds(newsFilter.getStateIds()) : "");
        queryParams.put("districtIds", newsFilter.getDistrictIds() != null ? getDistrictIds(newsFilter.getDistrictIds()) : "");
        queryParams.put("tehsilIds", newsFilter.getTehsilIds() != null ? getTehsilIds(newsFilter.getTehsilIds()) : "");
        queryParams.put("villageIds", newsFilter.getVillageIds() != null ? getVillageIds(newsFilter.getVillageIds()) : "");
        queryParams.put("status", newsFilter.getStatus() != null ? getStatusFilter(newsFilter.getStatus()) : "");
        queryParams.put("dateFilter", newsFilter.getDateFilter() != null ? getDateFilter(newsFilter.getDateFilter()) : "");

        queryParams.put("order", paginationFilter.getOrder().toString());
        queryParams.put("skip", paginationFilter.skip().toString());
        queryParams.put("limit", paginationFilter.getLimit().toString());
        queryParams.put("newsStatus", newsStatus.toString());

        return new StringSubstitutor(queryParams).replace(query);
    }


    public static String getLanguageFilter(String value) {
        String query = "filter doc.title.en == '${value}'";
        Map<String, String> template = Map.of("value", value);
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getCountryIds(Set<String> countryIds) {
        String query = "filter doc.countryIds any in ${value}";
        Map<String, String> template = Map.of("value", Jackson.toJsonString(countryIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getStateIds(Set<String> stateIds) {
        String query = "filter doc.stateIds any in ${value}";
        Map<String, String> template = Map.of("value", Jackson.toJsonString(stateIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getDistrictIds(Set<String> districtIds) {
        String query = "filter doc.districtIds any in ${value}";
        Map<String, String> template = Map.of("value", Jackson.toJsonString(districtIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getTehsilIds(Set<String> tehsilIds) {
        String query = "filter docs.tehsilIds any in ${value}";
        Map<String, String> template = Map.of("value", Jackson.toJsonString(tehsilIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    @SneakyThrows
    public static String getVillageIds(Set<String> villageIds) {
        String query = "filter doc.villageIds any in ${value}";
        Map<String, String> template = Map.of("value", Jackson.toJsonString(villageIds));
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    public static String getStatusFilter(Status status) {
        String query = "filter doc.status == '${status}'";
        Map<String, String> template = Map.of("status", status.toString());
        StringSubstitutor stringSubstitutor = new StringSubstitutor(template);
        return stringSubstitutor.replace(query);
    }

    // TODO: 08-06-2023 update here
    public static String getDateFilter(DateFilter dateFilter) {
        if (dateFilter.getStartDate() != null && dateFilter.getEndDate() != null) {
            String query = "filter doc.newsPublishDate >= @startDate And doc.newsPublishDate<= @endDate";
            Map<String, String> params = Map.of("startDate", dateFilter.getStartDate(),
                    "endDate", dateFilter.getEndDate());
            StringSubstitutor stringSubstitutor = new StringSubstitutor(params);
            return stringSubstitutor.replace(query);
        } else {
            return "";
        }
    }


    public String getUniqueSortedListBasedOnScore(String searchIt) {
        if (StringUtils.isBlank(searchIt)) {
            String template = "for doc in ${viewName}";
            Map<String, String> params = Map.of("viewName", NEWS_SEARCH);
            return new StringSubstitutor(params).replace(template);
        }
        SearchTokenHelper tokenHelper = new SearchTokenHelper(searchIt);

        String template = """         
                let searchedToken = tokens('${searchIt}', '${analyzer}')
                ${anyWordMatchingTokens}
                                
                for doc in ${viewName}
                search analyzer(
                    boost(${newsTitlePhraseFilters} or ${newsDescriptionPhraseFilters}, 4)
                    or
                    boost(${newsTitleAllTokenMatchFilters} or ${newsDescriptionAllTokenMatchFilters}, 3)
//                    or
//                    boost(${newsTitleLikeFilters} , 1)
                    ${anyWordMatchingTokenFilter}
                    ,
                    '${analyzer}'
                )
                filter doc.status != 'DELETED'
                let score = BM25(doc)
                sort score DESC
                """;

        Map<String, String> params = Map.of("searchIt", searchIt,
                "analyzer", "text_en",
                "viewName", NEWS_SEARCH,
                "anyWordMatchingTokens", getAnyWordMatchingTokens(tokenHelper),
                "newsTitlePhraseFilters", getNewsTitlePhraseFilters(),
                "newsDescriptionPhraseFilters", getNewsDescriptionPhraseFilters(),
                "newsTitleAllTokenMatchFilters", getNewsTitleAllTokenMatchFilters(),
                "newsDescriptionAllTokenMatchFilters", getNewsDescriptionAllTokenMatchFilters(),
                "anyWordMatchingTokenFilter", getAnyWordMatchingTokenFilters(tokenHelper)
//                "newsTitleLikeFilters",getNewsTitleLikeFilters()
        );

        return new StringSubstitutor(params).replace(template);
    }

//    private String getNewsDescriptionLikeFilters() {
//        List<String> descriptionFilter = getActiveLanguageCode().stream()
//                .map(languageCode -> "like(doc.description." + languageCode + ",searchedToken")
//                .toList();
//        return String.join("or", descriptionFilter);
//    }

//    private String getNewsTitleLikeFilters() {
//        List<String> likeFilter = getActiveLanguageCode().stream()
//                .map(languageCode -> "like(doc.title." + languageCode + " ,%searchedToken%)")
//                .toList();
//        return String.join(" or ", likeFilter);
//    }

    private String getNewsTitleAllTokenMatchFilters() {
        List<String> titleAllTokenMatchFiltersList = getActiveLanguageCode().stream()
                .map(languageCode -> "searchedToken all in doc.title." + languageCode)
                .toList();
        return String.join(" or ", titleAllTokenMatchFiltersList);
    }

    private String getNewsDescriptionAllTokenMatchFilters() {
        List<String> descriptionAllTokenMatchFiltersList = getActiveLanguageCode().stream()
                .map(languageCode -> "searchedToken all in doc.description." + languageCode)
                .toList();
        return String.join(" or ", descriptionAllTokenMatchFiltersList);
    }

    private String getNewsTitlePhraseFilters() {
        List<String> titlePhraseFilterList = getActiveLanguageCode().stream()
                .map(languageCode -> "phrase(doc.title." + languageCode + ", searchedToken)")
                .toList();
        return String.join(" or ", titlePhraseFilterList);
    }

    private String getNewsDescriptionPhraseFilters() {
        List<String> descriptionPhraseFilterList = getActiveLanguageCode().stream()
                .map(languageCode -> "phrase(doc.description." + languageCode + ", searchedToken)")
                .toList();
        return String.join(" or ", descriptionPhraseFilterList);
    }

    private String getAnyWordMatchingTokens(final SearchTokenHelper tokenHelper) {
        if (not(tokenHelper.isThereAnySearchableTokenWord())) {
            return "";
        }
        String template = "let anyWordMatchingToken = tokens('${onlySearchableTokenWord}', '${analyzer}')";

        Map<String, String> templateFiller = new HashMap<>();
        String onlySearchableTokenWord = tokenHelper.getOnlySearchableTokenWord();
        templateFiller.put("onlySearchableTokenWord", onlySearchableTokenWord);
        templateFiller.put("analyzer", "text_en");

        StringSubstitutor substitutor = new StringSubstitutor(templateFiller);
        return substitutor.replace(template);
    }

    private String getAnyWordMatchingTokenFilters(final SearchTokenHelper tokenHelper) {
        if (not(tokenHelper.isThereAnySearchableTokenWord())) {
            return "";
        }

        List<String> activeLanguageCodes = getActiveLanguageCode();
        String template = """
                or
                    boost(${titleAnyTokenMatchFilters} or ${descriptionAnyTokenMatchFilters}, 2)
                """;
        Map<String, String> templateFiller = new HashMap<>();

        List<String> titleAnyTokenMatchFiltersList = activeLanguageCodes.stream()
                .map(languageCode -> "doc.title." + languageCode + " in anyWordMatchingToken")
                .toList();
        String titleAnyTokenMatchFilters = String.join(" or ", titleAnyTokenMatchFiltersList);

        List<String> descriptionAnyTokenMatchFiltersList = activeLanguageCodes.stream()
                .map(languageCode -> "doc.description." + languageCode + " in anyWordMatchingToken")
                .toList();
        String descriptionAnyTokenMatchFilters = String.join(" or ", descriptionAnyTokenMatchFiltersList);

        templateFiller.put("titleAnyTokenMatchFilters", titleAnyTokenMatchFilters);
        templateFiller.put("descriptionAnyTokenMatchFilters", descriptionAnyTokenMatchFilters);

        StringSubstitutor substitutor = new StringSubstitutor(templateFiller);
        return substitutor.replace(template);
    }

    private List<String> getActiveLanguageCode() {
        String query = """
                for language in languages
                filter language.status== "ACTIVE"
                return language.code
                """;
        ArangoCursor<String> cursor = arangoOperations.query(query, String.class);
        try (cursor) {
            return cursor.asListRemaining();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }

}

