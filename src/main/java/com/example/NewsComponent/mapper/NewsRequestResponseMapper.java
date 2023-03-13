package com.example.NewsComponent.mapper;

import com.example.NewsComponent.domain.News;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public interface NewsRequestResponseMapper {

   // NewsRequestResponseMapper INSTANCE= Mappers.getMapper(NewsRequestResponseMapper.class);

    News getNewsForSaving(NewsRequest newsRequest);

    NewsResponse getNewsResponse(News news);

    //@Mapping(source = "news",target = "newsRequest")
   // @Mapping(target = "NewsRequest",ignore = true)
    News updateNews(@MappingTarget News news, NewsRequest newsRequest);

}
