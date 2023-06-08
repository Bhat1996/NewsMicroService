package com.example.NewsComponent.service.external;

import com.example.NewsComponent.dto.internal.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NotificationService(@Qualifier("notificationServiceRestTemplate") RestTemplate restTemplate,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public String sendNotification(NotificationRequest notificationRequest) {
        // TODO: 18-04-2023 put the url in application.properties file
        URI uri = URI.create("https://devapi.apnikheti.com/java-notification/api/send-notification");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + getTokenOfCurrentUser());

        String payloadInString = objectMapper.writeValueAsString(notificationRequest);

        HttpEntity<String> requestEntity = new HttpEntity<>(payloadInString, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }

    // TODO: 15-03-2023 check keycloak
    private synchronized String getTokenOfCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) principal;
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        return keycloakSecurityContext.getTokenString();

    }
}
