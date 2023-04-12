package com.example.NewsComponent.configuration.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("redis")
@Data
public class RedisCredential {
    private String host;
    private Integer port;
    private Integer database;
    private Integer timeout;
    private String password;
}
