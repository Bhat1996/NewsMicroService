package com.example.NewsComponent.configuration.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;


import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisDbCacheConfiguration {

    private final Map<String, RedisCacheConfiguration> cacheConfigurationMap;

    @Value("${redis.cache.default.duration}")
    private Integer defaultCacheDuration;

    public RedisDbCacheConfiguration(final CacheConfigMapGenerator cacheConfigMapGenerator) {
        cacheConfigurationMap = cacheConfigMapGenerator.cacheMap();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(defaultCacheDuration))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.withInitialCacheConfigurations(cacheConfigurationMap);
    }
}
