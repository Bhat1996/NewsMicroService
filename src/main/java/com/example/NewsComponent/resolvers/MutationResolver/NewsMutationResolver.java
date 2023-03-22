package com.example.NewsComponent.resolvers.MutationResolver;

import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.service.command.NewsCommandService;
import com.example.NewsComponent.validations.LanguageValidator;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Service
@Validated
public class NewsMutationResolver implements GraphQLMutationResolver {

    private  final NewsCommandService newsCommandService;

    private  final LanguageValidator languageValidator;
    public NewsMutationResolver(NewsCommandService newsCommandService, LanguageValidator languageValidator) {
        this.newsCommandService = newsCommandService;
        this.languageValidator = languageValidator;
    }

    public NewsResponse savedNews(@Valid NewsRequest newsRequest){
        languageValidator.languageValidateChecker(newsRequest);
        return newsCommandService.saveNewsResponse(newsRequest);
    }

   public NewsResponse publishNews(@NotBlank String newsId){
        return  newsCommandService.publishNews(newsId);
   }

   public  NewsResponse publishAndNotify(@NotBlank String newsId){
        return newsCommandService.publishAndNotify(newsId);
   }

   public String deleteNews(@NotBlank String id){
        return newsCommandService.deleteNews(id);
   }

   public String updateNews(@Valid NewsRequest newsRequest) {
       languageValidator.languageValidateChecker(newsRequest);
        return newsCommandService.updateNews(newsRequest);
   }
}
