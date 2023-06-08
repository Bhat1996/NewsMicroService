package com.example.NewsComponent.service.external;

import com.example.NewsComponent.dto.response.GraphqlResponseMapper;
import com.example.NewsComponent.utils.GraphqlResponseErrorUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
public class UserService {
    private final RestTemplate restTemplate;

    public UserService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getIdOfCurrentUser() {
        // TODO: 18-04-2023 add url in application.properties file
        String userUrl = "http://112.196.108.244:9008/graphql";
        String query = """
                    query {
                         getIdOfUser
                    }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getTokenOfCurrentUser());
        headers.add("content-type", "application/graphql");

        ResponseEntity<GraphqlResponseMapper<String>> responseEntity =
                restTemplate.exchange(userUrl, HttpMethod.POST,
                        new HttpEntity<>(query, headers),
                        new ParameterizedTypeReference<>() {
                        });

        GraphqlResponseMapper<String> body = responseEntity.getBody();
        if (Objects.requireNonNull(body).getErrors() != null) {
            body.getErrors().forEach(GraphqlResponseErrorUtils::throwRightException);
            return null;
        }

        Map<String, String> data = body.getData();
        return data.get("getIdOfUser");
    }

    public synchronized String getTokenOfCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) principal;
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        return keycloakSecurityContext.getTokenString();
    }

//    @SneakyThrows
//    public List<IdAndNameResponse> getUserFullNames(final List<String> ids) {
//        String userUrl = "http://112.196.108.244:9008/graphql";
//
//        String query = """
//                    query{
//                         userFullNames(ids: ${ids}) {
//                           id
//                           fullName
//                         }
//                       }
//                """;
//
//        StringSubstitutor substitutor =
//                new StringSubstitutor(Map.of("ids", objectMapper.writeValueAsString(ids)));
//        String finalQuery = substitutor.replace(query);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + getTokenOfCurrentUser());
//        headers.add("content-type", "application/graphql");
//
//        ResponseEntity<GraphqlResponseMapper<List<IdAndNameResponse>>> responseEntity =
//                restTemplate.exchange(userUrl, HttpMethod.POST, new HttpEntity<>(finalQuery,headers),
//                        new ParameterizedTypeReference<>() {
//                        });
//
//        GraphqlResponseMapper<List<IdAndNameResponse>> body = responseEntity.getBody();
//        if (Objects.requireNonNull(body).getErrors() != null) {
//            body.getErrors().forEach(GraphqlResponseErrorUtils::throwRightException);
//            return null;
//        }
//
//        Map<String, List<IdAndNameResponse>> data = body.getData();
//        return data.get("userFullNames");
//    }

}
