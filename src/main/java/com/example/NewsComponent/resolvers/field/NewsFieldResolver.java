package com.example.NewsComponent.resolvers.field;

import com.example.NewsComponent.configuration.context.DataLoaderRegistryFactory;
import com.example.NewsComponent.dto.response.NewsResponse;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NewsFieldResolver implements GraphQLResolver<NewsResponse> {

    public CompletableFuture<Long> numberOfLikes(NewsResponse newsResponse,
                                                 DataFetchingEnvironment dataFetchingEnvironment){
        DataLoader<String, Long> dataLoader =
                dataFetchingEnvironment.getDataLoader(DataLoaderRegistryFactory.NUMBER_OF_LIKES_LOADER);
        return dataLoader.load(newsResponse.getId());
    }

    public CompletableFuture<Long> numberOfComments(NewsResponse newsResponse,
                                                      DataFetchingEnvironment dataFetchingEnvironment){
        DataLoader<String, Long> dataLoader =
                dataFetchingEnvironment.getDataLoader(DataLoaderRegistryFactory.NUMBER_OF_COMMENTS_LOADER);
        return dataLoader.load(newsResponse.getId());
    }
}
