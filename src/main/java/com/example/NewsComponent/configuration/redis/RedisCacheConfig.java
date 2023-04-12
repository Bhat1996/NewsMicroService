package com.example.NewsComponent.configuration.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("redis.cache")
@Data
public class RedisCacheConfig {

    private List<CacheConfig> list;

    @Data
    public static class CacheConfig {
        private String name;
        private Integer duration;
    }

}
