package com.example.NewsComponent.configuration.context;

import com.example.NewsComponent.service.query.CommonQueryService;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.MappedBatchLoader;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class DataLoaderRegistryFactory {

    public static final String NUMBER_OF_LIKES_LOADER = "NUMBER_OF_LIKES_LOADER ";
    public static final String NUMBER_OF_COMMENTS_LOADER = "NUMBER_OF_COMMENTS_LOADER ";

    private static final Executor dataLoaderPool = new DelegatingSecurityContextExecutorService(
            Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()));
    private final CommonQueryService commonQueryService;

    public DataLoaderRegistryFactory(CommonQueryService commonQueryService) {
        this.commonQueryService = commonQueryService;
    }

    public DataLoaderRegistry create() {
        var dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(NUMBER_OF_LIKES_LOADER, createLikesLoader());
        dataLoaderRegistry.register(NUMBER_OF_COMMENTS_LOADER, createCommentsLoader());
        return dataLoaderRegistry;
    }

    private DataLoader<?, ?> createCommentsLoader() {
        final MappedBatchLoader<String, Long> mappedBatchLoader = keys ->
                CompletableFuture.supplyAsync(
                        () -> commonQueryService.getNumberOfComments(keys),
                        dataLoaderPool
                );
        return DataLoaderFactory.newMappedDataLoader(mappedBatchLoader);
    }

    private DataLoader<String, Long> createLikesLoader() {
        final MappedBatchLoader<String, Long> mappedBatchLoader = keys ->
                CompletableFuture.supplyAsync(
                        () -> commonQueryService.getNumberOfLikes(keys),
                        dataLoaderPool
                );
        return DataLoaderFactory.newMappedDataLoader(mappedBatchLoader);
    }

}
