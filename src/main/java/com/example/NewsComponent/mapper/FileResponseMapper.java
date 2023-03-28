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

    public NewsResponse getNewsResponseWithFiles(String id, NewsResponse newsResponse){
        List<FileResults> files = fileRepository.getFiles(List.of(id));
        FileResults results = files.stream().filter(fileResults ->
                        Objects.equals(fileResults.getId(), id))
                .findFirst().orElse(new FileResults());

        newsResponse.setImageUrls(results.getImageUrls());
        newsResponse.setVideoUrls(results.getVideoUrls());
        newsResponse.setDocumentUrls(results.getDocumentUrls());
        newsResponse.setAudioUrls(results.getAudioUrls());

        return newsResponse;
    }

}
