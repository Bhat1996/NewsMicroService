package com.example.NewsComponent.mapper;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.domain.vertex.File;
import com.example.NewsComponent.dto.internal.FileResults;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.repository.vertex.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileResponseMapper {

    private final FileRepository fileRepository;

    public NewsResponse getNewsResponseWithFiles(News news){
        List<FileResults> files = fileRepository.getFiles(List.of(news.getId()));
        FileResults results = files.stream().filter(fileResults ->
                        Objects.equals(fileResults.getId(), news.getId()))
                .findFirst().orElse(new FileResults());

        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setId(news.getId());
        newsResponse.setArangoId(news.getArangoId());
        newsResponse.setNewsStatus(news.getNewsStatus());
        newsResponse.setInterestIds(news.getInterestIds());
        newsResponse.setHashTagIds(news.getHashTagIds());
        newsResponse.setDescription(news.getDescription());
        newsResponse.setNewsPublishDate(LocalDateTime.now());
        newsResponse.setRemarks(news.getRemarks());
        newsResponse.setNewsSource(news.getNewsSource());
        newsResponse.setSourceLink(news.getSourceLink());
        newsResponse.setSlugTitle(news.getSlugTitle());
        newsResponse.setStatus(news.getStatus());
        newsResponse.setCountryIds(news.getCountryIds());
        newsResponse.setImageUrls(results.getImageUrls());
        newsResponse.setVideoUrls(results.getVideoUrls());
        newsResponse.setDocumentUrls(results.getDocumentUrls());
        newsResponse.setAudioUrls(results.getAudioUrls());

        return newsResponse;
    }

}
