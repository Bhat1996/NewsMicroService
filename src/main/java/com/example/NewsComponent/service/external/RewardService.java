package com.example.NewsComponent.service.external;

import com.amazonaws.util.json.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RewardService {
    private final RestTemplate restTemplate;

    public RewardService(final @Qualifier("notificationServiceRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void triggerRewardService(final String id,
                                     final String userId,
                                     final String tokenOfCurrentUser,
                                     final String url) {
        try {
            log.info("Going To Trigger Reward Service For Url {}, Id Is {} And User Id Is {}",
                    url, id, userId);

            Map<String, String> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("userId", userId);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfCurrentUser);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> httpEntity = new HttpEntity<>(Jackson.toJsonString(payload), httpHeaders);

            String result = restTemplate.postForObject(url , httpEntity, String.class);
            log.info("Result Of Trigger Reward Service For Url::{} IS:: {}",url,  result);
        } catch (Exception exception) {
            log.error("Exception In Triggering Reward Service On Url {}, Question Id {}, User Id {}",
                    url, id, userId, exception);
        }
    }
}
