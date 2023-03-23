package com.example.NewsComponent.service.command;

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
import com.example.NewsComponent.repository.vertex.FileRepository;
import com.example.NewsComponent.service.external.NotificationService;
import com.example.NewsComponent.service.transaction.Action;
import com.example.NewsComponent.service.transaction.TransactionalWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.NewsComponent.metadata.EdgeName.*;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Service
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final NewsHasInterestRepository newsHasInterestRepository;
    private final NewsHasHashTagRepository newsHasHashTagRepository;
    private final NotificationService notificationService;
    private final TransactionalWrapper transactionalWrapper;
    private final NewsIsForLocationRepository newsIsForLocationRepository;
    private final FileRepository fileRepository;

    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsCommandService(NewsRepository newsRepository,
                              NewsHasInterestRepository newsHasInterestRepository,
                              NewsHasHashTagRepository newsHasHashTagRepository,
                              NotificationService notificationService,
                              TransactionalWrapper transactionalWrapper,
                              NewsIsForLocationRepository newsIsForLocationRepository,
                              FileRepository fileRepository, NewsRequestResponseMapper newsRequestResponseMapper) {
        this.newsRepository = newsRepository;
        this.newsHasInterestRepository = newsHasInterestRepository;
        this.newsHasHashTagRepository = newsHasHashTagRepository;
        this.notificationService = notificationService;
        this.transactionalWrapper = transactionalWrapper;
        this.newsIsForLocationRepository = newsIsForLocationRepository;
        this.fileRepository = fileRepository;

        this.newsRequestResponseMapper = newsRequestResponseMapper;
    }

    //TODO media
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
        News savedNews = transactionalWrapper.executeInsideTransaction
                (Set.of(NEWS, NEWS_HAS_INTEREST, NEWS_HAS_HASHTAG, NEWS_IS_FOR_LOCATION), action);
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
        locationIds.addAll(Optional.ofNullable(stateIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(districtIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(tehsilIds).orElse(new HashSet<>()));
        locationIds.addAll(Optional.ofNullable(villageIds).orElse(new HashSet<>()));
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

    public NewsResponse publishAndNotify(String newsId) {
        News newsById = newsRepository.getNewsById(newsId);

        Location location = getPublishedLocationForNotify(newsById);
        Set<String> languageCodes =  newsById.getTitle().keySet();
        String language = String.join(",", languageCodes);
        location.setLang(language);

        Interests interests = new Interests();
        interests.setKeywordName(String.join(",", newsById.getInterestIds()));

        Filter filter = getFilter(location, interests);

        Messages messages = new Messages();
        messages.setContent(newsById.getTitle());

        NotificationRequest notificationRequest = getNotificationRequest(newsId, filter, messages);
        // TODO: 20-03-2023 it
        //notificationService.sendNotification(notificationRequest);
        return publishNews(newsId);
    }

    public static Location getPublishedLocationForNotify(News newsById) {
        Location location = new Location();

        location.setCountryID(String.join(",", newsById.getCountryIds()));
        location.setStateID(String.join(",", newsById.getStateIds()));
        location.setDistrictID(String.join(",", newsById.getDistrictIds()));
        location.setTehsilID(String.join(",", newsById.getTehsilIds()));
        location.setVillageID(String.join(",", newsById.getVillageIds()));
        return location;
    }

    public static Filter getFilter(Location location, Interests interests) {
        Filter filter = new Filter();
        filter.setApp("Apni Kheti");
        filter.setPhone("");
        filter.setInterests(interests);
        filter.setLocation(location);
        return filter;
    }

    public static NotificationRequest getNotificationRequest(String newsId, Filter filter, Messages messages) {
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
        return notificationRequest;
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
