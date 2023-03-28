package com.example.NewsComponent.resolvers.mutation;

import com.example.NewsComponent.dto.response.NewsResponse;
import com.example.NewsComponent.validations.LanguageValidator;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import com.example.NewsComponent.dto.request.NewsRequest;
import com.example.NewsComponent.mapper.FileInputMapper;
import com.example.NewsComponent.service.command.NewsCommandService;
import com.example.NewsComponent.validations.afterBinding.NewsValidator;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Service
@Validated
@AllArgsConstructor
public class NewsMutationResolver implements GraphQLMutationResolver {

    private  final NewsCommandService newsCommandService;
    private  final LanguageValidator languageValidator;
    private  final NewsValidator newsValidator;



    public NewsResponse savedNews(@Valid NewsRequest newsRequest ,
                                  final DataFetchingEnvironment dataFetchingEnvironment){
        DefaultGraphQLServletContext context = dataFetchingEnvironment.getContext();

        HttpServletRequest httpServletRequest = context.getHttpServletRequest();
        String contentType = httpServletRequest.getContentType();
        if (contentType.contains("multipart/")) {
            Map<String, List<Part>> parts = context.getParts();

            FileInputMapper fileInputMapper = new FileInputMapper(newsRequest,parts);
            FileInputWithPart fileInputWithPart = fileInputMapper.getFileInputWithPart();

            newsValidator.validateMultipartFile(fileInputWithPart, httpServletRequest);
            languageValidator.languageValidateChecker(newsRequest);
            return newsCommandService.saveNewsResponse(newsRequest, fileInputWithPart);

        } else {
            FileInputMapper fileInputMapper = new FileInputMapper(newsRequest, Map.of());
            FileInputWithPart fileInputWithPart = fileInputMapper.getFileInputWithPart();
            languageValidator.languageValidateChecker(newsRequest);
            return newsCommandService.saveNewsResponse(newsRequest, fileInputWithPart);
        }

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
