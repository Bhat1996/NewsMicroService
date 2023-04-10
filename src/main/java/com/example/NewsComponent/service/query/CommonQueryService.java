package com.example.NewsComponent.service.query;

import com.example.NewsComponent.dto.internal.LikesDto;
import com.example.NewsComponent.repository.edge.NewsLikedByRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CommonQueryService {

    private final NewsLikedByRepository newsLikedByRepository;

    public CommonQueryService(NewsLikedByRepository newsLikedByRepository) {
        this.newsLikedByRepository = newsLikedByRepository;
    }

    public Map<String, Long> getNumberOfLikes(Set<String> ids){
        List<LikesDto> likesDtos = newsLikedByRepository.countNumberOfLikes(ids);
        Map<String, Long> result = new HashMap<>();
        likesDtos.forEach(likesDto -> result.put(likesDto.getId(), likesDto.getTotal()));
        return result;
    }
}
