package com.example.NewsComponent.resolvers.newsMutationResolver;

import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.service.commandService.NewsCommandService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class NewsMutationResolver implements GraphQLMutationResolver {

    private  final NewsCommandService newsCommandService;

    public NewsMutationResolver(NewsCommandService newsCommandService) {
        this.newsCommandService = newsCommandService;
    }

    public NewsResponse savedNews(@Valid NewsRequest newsRequest){
        return newsCommandService.saveNewsResponse(newsRequest);
    }

   public NewsResponse publishNews(String newsId){
        return  newsCommandService.publishNews(newsId);
   }

   public String deleteNews(String id){
        return newsCommandService.deleteNews(id);
   }

   public String updateNews(NewsRequest newsRequest){
        return newsCommandService.updateNews(newsRequest);
   }
}
