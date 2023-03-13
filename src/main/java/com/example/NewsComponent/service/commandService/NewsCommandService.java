package com.example.NewsComponent.service.commandService;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.enums.NewsStatus;
import com.example.NewsComponent.enums.Status;
import com.example.NewsComponent.mapper.NewsRequestResponseMapper;
import com.example.NewsComponent.repository.NewsRepository;
import com.example.NewsComponent.service.transaction.Action;
import com.example.NewsComponent.service.transaction.TransactionalWrapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class NewsCommandService {

    private final NewsRepository newsRepository;
    private final TransactionalWrapper transactionalWrapper;

    private final NewsRequestResponseMapper newsRequestResponseMapper;

    public NewsCommandService(NewsRepository newsRepository,
                              TransactionalWrapper transactionalWrapper,
                              NewsRequestResponseMapper newsRequestResponseMapper) {
        this.newsRepository = newsRepository;
        this.transactionalWrapper = transactionalWrapper;

        this.newsRequestResponseMapper = newsRequestResponseMapper;
    }

    public NewsResponse saveNewsResponse(NewsRequest newsRequest) {
        News newsForSaving = newsRequestResponseMapper.getNewsForSaving(newsRequest);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.saveNews(arangoDatabase, transactionId, newsForSaving);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);
    }

    // TODO: 10-03-2023 "updated case"
    public String updateNews(NewsRequest newsRequest) {
        News news = newsRepository.getNewsById(newsRequest.getId());
        News newsForSaving = newsRequestResponseMapper.updateNews(news, newsRequest);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase, transactionId, newsForSaving);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
        newsRequestResponseMapper.getNewsResponse(savedNews);
        return "updated";
    }

    public NewsResponse publishNews(String newsId) {
        News getNews = newsRepository.getNewsById(newsId);
        getNews.setNewsStatus(NewsStatus.PUBLISHED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase,transactionId,getNews);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);

    }

    public String deleteNews(String id) {
        News news = newsRepository.getNewsById(id);
        news.setStatus(Status.DELETED);
        Action<News> action = (arangoDatabase, transactionId) ->
                newsRepository.updateNews(arangoDatabase,transactionId,news);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);

        return "deleted";
    }


}
