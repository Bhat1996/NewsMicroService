package com.example.NewsComponent.service.commandService;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.domain.edge.NewsHasHashTag;
import com.example.NewsComponent.domain.edge.NewsHasInterest;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.repository.edge.NewsHasInterestRepository;
import com.example.NewsComponent.service.transaction.Action;
import com.example.NewsComponent.service.transaction.TransactionalWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.example.NewsComponent.metadata.EdgeName.NEWS_HAS_HASHTAG;
import static com.example.NewsComponent.metadata.EdgeName.NEWS_HAS_INTEREST;
import static com.example.NewsComponent.metadata.VertexName.NEWS;

@Service
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final NewsHasInterestRepository newsHasInterestRepository;
    private final TransactionalWrapper transactionalWrapper;

    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsCommandService(NewsRepository newsRepository,
                              NewsHasInterestRepository newsHasInterestRepository, TransactionalWrapper transactionalWrapper,
                              NewsRequestResponseMapper newsRequestResponseMapper) {
        this.newsRepository = newsRepository;
        this.newsHasInterestRepository = newsHasInterestRepository;
        this.transactionalWrapper = transactionalWrapper;

        this.newsRequestResponseMapper = newsRequestResponseMapper;
    }

    //TODO update it
    public NewsResponse saveNewsResponse(NewsRequest newsRequest) {
        News newsForSaving = newsRequestResponseMapper.getNewsForSaving(newsRequest);

        List<String> interestIds = newsRequest.getInterestIds();
        List<String> hashtagsIds = newsRequest.getHashTagIds();

        List<NewsHasHashTag> newsHasHashTagList = getNewsHasHashTagList(newsForSaving, hashtagsIds);

        Action<News> action = (arangoDatabase, transactionId) -> {
            News news = newsRepository.saveNews(arangoDatabase, transactionId, newsForSaving);
            List<NewsHasInterest> newsHasInterestList = getNewsHasInterests(news, interestIds);
            newsHasInterestList.forEach(newsHasInterest ->
                    newsHasInterestRepository.saveNewsHasInterestEdge(arangoDatabase, transactionId, newsHasInterest));
             return news;
        };
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of(NEWS,NEWS_HAS_INTEREST,NEWS_HAS_HASHTAG), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);
    }

    @NotNull
    public static List<NewsHasHashTag> getNewsHasHashTagList(News newsForSaving, List<String> hashtagsIds) {
        return hashtagsIds.stream().map(hashTagIds -> {
            NewsHasHashTag newsHasHashTag = new NewsHasHashTag();
            newsHasHashTag.set_from(newsForSaving.getId());
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

    public String deleteNews(String id) {
        News news = newsRepository.getNewsById(id);
        news.setStatus(Status.DELETED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, news);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("news"), action);

        return "deleted";
    }


}
