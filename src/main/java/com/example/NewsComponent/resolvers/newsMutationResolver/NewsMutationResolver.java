package com.example.NewsComponent.resolvers.newsMutationResolver;

import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.service.commandService.NewsCommandService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.Part;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

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

   public NewsResponse publishNews(@NotBlank String newsId){
        return  newsCommandService.publishNews(newsId);
   }

   public String deleteNews(@NotBlank String id){
        return newsCommandService.deleteNews(id);
   }

   public String updateNews(@Valid NewsRequest newsRequest){
        return newsCommandService.updateNews(newsRequest);
   }
}
