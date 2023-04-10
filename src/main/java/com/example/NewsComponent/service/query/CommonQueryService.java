package com.example.NewsComponent.service.query;

import com.example.NewsComponent.dto.internal.ResultDto;
import com.example.NewsComponent.repository.edge.NewsHasCommentRepository;
import com.example.NewsComponent.repository.edge.NewsLikedByRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CommonQueryService {

    private final NewsLikedByRepository newsLikedByRepository;
    private  final NewsHasCommentRepository newsHasCommentRepository;

    public CommonQueryService(NewsLikedByRepository newsLikedByRepository,
                              NewsHasCommentRepository newsHasCommentRepository) {
        this.newsLikedByRepository = newsLikedByRepository;
        this.newsHasCommentRepository = newsHasCommentRepository;
    }

    public Map<String, Long> getNumberOfLikes(Set<String> ids){
        List<ResultDto> resultDtos = newsLikedByRepository.countNumberOfLikes(ids);
        Map<String, Long> result = new HashMap<>();
        resultDtos.forEach(resultDto -> result.put(resultDto.getId(), resultDto.getTotal()));
        return result;
    }
    public Map<String, Long> getNumberOfComments(Set<String> ids){
        List<ResultDto> resultDtos = newsHasCommentRepository.countNoOfComments(ids);
        Map<String, Long> result = new HashMap<>();
        resultDtos.forEach(resultDto -> result.put(resultDto.getId(), resultDto.getTotal()));
        return result;
    }
}
