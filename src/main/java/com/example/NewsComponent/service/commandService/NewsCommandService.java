package com.example.NewsComponent.service.commandService;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.domain.edge.NewsHasHashTag;
import com.example.NewsComponent.domain.edge.NewsHasInterest;
import com.example.NewsComponent.domain.edge.NewsIsForLocation;
import com.example.NewsComponent.dto.internal.*;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.repository.edge.NewsHasHashTagRepository;
import com.example.NewsComponent.repository.edge.NewsHasInterestRepository;
import com.example.NewsComponent.repository.edge.NewsIsForLocationRepository;
import com.example.NewsComponent.service.transaction.Action;
import com.example.NewsComponent.service.transaction.TransactionalWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.NewsComponent.metadata.EdgeName.*;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Service
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final NewsHasInterestRepository newsHasInterestRepository;
    private final NewsHasHashTagRepository newsHasHashTagRepository;
    private final TransactionalWrapper transactionalWrapper;
    private final NewsIsForLocationRepository newsIsForLocationRepository;

    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsCommandService(NewsRepository newsRepository,
                              NewsHasInterestRepository newsHasInterestRepository, NewsHasHashTagRepository newsHasHashTagRepository, TransactionalWrapper transactionalWrapper,
                              NewsIsForLocationRepository newsIsForLocationRepository, NewsRequestResponseMapper newsRequestResponseMapper) {
        this.newsRepository = newsRepository;
        this.newsHasInterestRepository = newsHasInterestRepository;
        this.newsHasHashTagRepository = newsHasHashTagRepository;
        this.transactionalWrapper = transactionalWrapper;
        this.newsIsForLocationRepository = newsIsForLocationRepository;

        this.newsRequestResponseMapper = newsRequestResponseMapper;
    }

    //TODO media and publish location
    public NewsResponse saveNewsResponse(NewsRequest newsRequest) {
        News newsForSaving = newsRequestResponseMapper.getNewsForSaving(newsRequest);

        List<String> interestIds = newsRequest.getInterestIds();
        List<String> hashtagsIds = newsRequest.getHashTagIds();
        Set<String> locationIds = getLocationIds(newsRequest);


        Action<News> action = (arangoDatabase, transactionId) -> {

            News news = newsRepository.saveNews(arangoDatabase, transactionId, newsForSaving);

            List<NewsIsForLocation> newsIsForCountryList = getNewsIsForCountryList(locationIds, news);
            newsIsForCountryList.forEach(newsIsForLocation ->
                    newsIsForLocationRepository.saveNewsForLocation(arangoDatabase, transactionId, newsIsForLocation));

            List<NewsHasInterest> newsHasInterestList = getNewsHasInterests(news, interestIds);
            newsHasInterestList.forEach(newsHasInterest ->
                    newsHasInterestRepository.saveNewsHasInterestEdge(arangoDatabase, transactionId, newsHasInterest));

            List<NewsHasHashTag> newsHasHashTagList = getNewsHasHashTagList(news, hashtagsIds);
            newsHasHashTagList.forEach(newsHasHashTag ->
                    newsHasHashTagRepository.saveNewsHasHashtagsEdge(arangoDatabase, transactionId, newsHasHashTag));
            return news;

        };
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of(NEWS, NEWS_HAS_INTEREST, NEWS_HAS_HASHTAG, NEWS_IS_FOR_LOCATION), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);
    }

    public static Set<String> getLocationIds(NewsRequest newsRequest) {
        Set<String> countryIds = newsRequest.getCountryIds();
        Set<String> stateIds = newsRequest.getStateIds();
        Set<String> districtIds = newsRequest.getDistrictIds();
        Set<String> tehsilIds = newsRequest.getTehsilIds();
        Set<String> villageIds = newsRequest.getVillageIds();

        Set<String> locationIds = new HashSet<>();
        locationIds.addAll(countryIds);
        locationIds.addAll(stateIds);
        locationIds.addAll(districtIds);
        locationIds.addAll(tehsilIds);
        locationIds.addAll(villageIds);
        return locationIds;
    }


    public static List<NewsIsForLocation> getNewsIsForCountryList(Set<String> locationIds, News news) {
        return locationIds.stream().map(locationId -> {
            NewsIsForLocation newsIsForLocation = new NewsIsForLocation();
            newsIsForLocation.set_from(news.getArangoId());
            newsIsForLocation.set_to(locationId);
            newsIsForLocation.setCreatedDate(LocalDateTime.now());
            newsIsForLocation.setModifiedDate(LocalDateTime.now());
            return newsIsForLocation;
        }).toList();
    }

    public static List<NewsHasHashTag> getNewsHasHashTagList(News newsForSaving, List<String> hashtagsIds) {
        return hashtagsIds.stream().map(hashTagIds -> {
            NewsHasHashTag newsHasHashTag = new NewsHasHashTag();
            newsHasHashTag.set_from(newsForSaving.getArangoId());
            newsHasHashTag.set_to(hashTagIds);
            newsHasHashTag.setCreatedDate(LocalDateTime.now());
            newsHasHashTag.setModifiedDate(LocalDateTime.now());
            return newsHasHashTag;
        }).toList();
    }

    public static List<NewsHasInterest> getNewsHasInterests(News newsForSaving, List<String> interestIds) {
        List<NewsHasInterest> newsHasInterestList = interestIds.stream().map(interestId -> {
            NewsHasInterest newsHasInterest = new NewsHasInterest();
            newsHasInterest.set_from(newsForSaving.getArangoId());
            newsHasInterest.set_to(interestId);
            newsHasInterest.setCreatedDate(LocalDateTime.now());
            newsHasInterest.setModifiedDate(LocalDateTime.now());
            return newsHasInterest;
        }).toList();
        return newsHasInterestList;
    }


    public String updateNews(NewsRequest newsRequest) {
        News news = newsRepository.getNewsById(newsRequest.getId());
        News newsForSaving = newsRequestResponseMapper.updateNews(news, newsRequest);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, newsForSaving);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("news"), action);
        newsRequestResponseMapper.getNewsResponse(savedNews);
        return "updated";
    }

    public NewsResponse publishNews(String newsId) {
        News getNews = newsRepository.getNewsById(newsId);
        getNews.setNewsStatus(NewsStatus.PUBLISHED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, getNews);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("news"), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);

    }

    //TODO add notify method to this
    public NewsResponse publishAndNotify(String newsId) {
        News newsById = newsRepository.getNewsById(newsId);
        Set<String> countryIds = newsById.getCountryIds();
        Set<String> stateIds = newsById.getStateIds();
        Set<String> districtIds = newsById.getDistrictIds();
        Set<String> tehsilIds = newsById.getTehsilIds();
        Set<String> villageIds = newsById.getVillageIds();

        Location location = new Location();
        String countryId = String.join(",", countryIds);
        String stateId = String.join(",", stateIds);
        String districtId = String.join(",", districtIds);
        String tehsilId = String.join(",", tehsilIds);
        String villageId = String.join(",", villageIds);

        location.setCountryID(countryId);
        location.setStateID(stateId);
        location.setDistrictID(districtId);
        location.setTehsilID(tehsilId);
        location.setVillageID(villageId);

        String en = newsById.getTitle().getEn();
        String pb = newsById.getTitle().getPb();

        String language = "hn";
        if (StringUtils.isNotBlank(en)) {
            language = String.join(",", "en");
        }
        if (StringUtils.isNotBlank(pb)) {
            language = String.join(",", "pb");
        }
        location.setLang(language);

        List<String> interestIds = newsById.getInterestIds();
        String interestId = String.join(",", interestIds);

        Interests interests = new Interests();
        interests.setKeywordName(interestId);

        Filter filter = new Filter();
        filter.setApp("Apni Kheti");
        filter.setPhone("");
        filter.setInterests(interests);
        filter.setLocation(location);

        Messages messages = new Messages();
        messages.setContent(newsById.getTitle());


        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setAction(newsId);
        notificationRequest.setPriority("Normal");
        notificationRequest.setFilter(filter);
        notificationRequest.setMessages(messages);
        notificationRequest.setValue("News Details");
        notificationRequest.setRequestID("1");
        notificationRequest.setTranslations(List.of());
        notificationRequest.setPostedBy("ak");
        notificationRequest.setUserImage("");

    }

    public String deleteNews(String id) {
        News news = newsRepository.getNewsById(id);
        news.setStatus(Status.DELETED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, news);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("news"), action);

        return "deleted";
    }


}
