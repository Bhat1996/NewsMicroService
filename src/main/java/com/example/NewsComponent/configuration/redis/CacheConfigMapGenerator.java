package com.example.NewsComponent.configuration.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfigMapGenerator {
    private final RedisCacheConfig redisCacheConfig;

    public CacheConfigMapGenerator(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = redisCacheConfig;
    }

    public Map<String, RedisCacheConfiguration> cacheMap() {
        Map<String, RedisCacheConfiguration> cacheMap = new HashMap<>();
        for(RedisCacheConfig.CacheConfig config: redisCacheConfig.getList()) {
            RedisCacheConfiguration configuration = getRedisConfiguration(config.getDuration());
            cacheMap.put(config.getName(), configuration);
        }
        return cacheMap;
    }

    private RedisCacheConfiguration getRedisConfiguration(final Integer duration) {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(duration))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
