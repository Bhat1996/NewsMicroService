package com.example.NewsComponent.service.query;

import com.example.NewsComponent.repository.edge.NewsLikedByRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CommonQueryService {

    private final NewsLikedByRepository newsLikedByRepository;

    public CommonQueryService(NewsLikedByRepository newsLikedByRepository) {
        this.newsLikedByRepository = newsLikedByRepository;
    }

    public Map<String, Long> getNumberOfLikes(Set<String> ids){
        return Map.of();
    }
}
