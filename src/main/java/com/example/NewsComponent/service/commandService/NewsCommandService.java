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
        Action<News> action = (arangoDatabase, transactionId) -> newsRepository.save(newsForSaving);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
        return newsRequestResponseMapper.getNewsResponse(savedNews);
    }

    // TODO: 10-03-2023 "updated case"
    public String updateNews(NewsRequest newsRequest) {
        News news = newsRepository.getNewsById(newsRequest.getId());
        //News news = newsRepository.findById(newsRequest.getId());
        News newsForSaving = newsRequestResponseMapper.getNewsForSaving(newsRequest);
        Action<News> action = (arangoDatabase, transactionId) -> newsRepository.save(newsForSaving);
        News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
        newsRequestResponseMapper.getNewsResponse(savedNews);
        return "updated";
    }

    public NewsResponse publishNews(String newsId) {
        Optional<News> optionalNews = newsRepository.findById(newsId);

        if (optionalNews.isPresent()) {
            News news = optionalNews.get();
            news.setNewsStatus(NewsStatus.PUBLISHED);
            Action<News> action = (arangoDatabase, transactionId) -> newsRepository.save(news);
            News savedNews = transactionalWrapper.executeInsideTransaction(Set.of("NEWS"), action);
            return newsRequestResponseMapper.getNewsResponse(savedNews);
        } else {
            throw new RuntimeException("News with this id is not found");
        }
    }

    public String deleteNews(String id) {
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            News news1 = news.get();
            news1.setStatus(Status.DELETED);
            newsRepository.save(news1);

        }
        return "deleted";
    }


}
