package com.example.NewsComponent.configuration.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("aws")
@Data
public class S3Credential {
    private String region;
    private String accessKey;
    private String secretKey;
}
