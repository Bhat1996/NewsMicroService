package com.example.NewsComponent.mapper;

import com.example.NewsComponent.dto.vertex.News;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.dto.request.NewsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public interface NewsRequestResponseMapper {

    News getNewsForSaving(NewsRequest newsRequest);

    NewsResponse getNewsResponse(News news);

    News updateNews(@MappingTarget News news, NewsRequest newsRequest);


}
