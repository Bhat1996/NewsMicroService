package com.example.NewsComponent.utils;

import com.arangodb.ArangoDBException;
import com.example.NewsComponent.dto.response.GraphqlErrorResponse;
import com.example.NewsComponent.exceptions.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class GraphqlResponseErrorUtils {
    private GraphqlResponseErrorUtils() {
        throw new UnsupportedOperationException("No Object For This Class..!");
    }

    public static void throwRightException(GraphqlErrorResponse graphqlErrorResponse) {
        String message = graphqlErrorResponse.getMessage();
        Map<String, Object> extensions = graphqlErrorResponse.getExtensions();

        int code = (int) extensions.get("statusCode");
        if (code == HttpStatus.NOT_FOUND.value()) {
            throw new ResourceNotFoundException(message);
        } else if (code == HttpStatus.FORBIDDEN.value()) {
            throw new CustomAccessDeniedException(message);
        } else if (code == HttpStatus.UNAUTHORIZED.value()) {
            throw new CustomAccessDeniedException(message);
        } else if (code == HttpStatus.CONFLICT.value()) {
            throw new CustomDataViolationException(message, extensions);
        } else if (code == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
            throw new CustomDataViolationException(message, extensions);
        } else {
            throw new InternalServerException(message);
        }
    }

    public static void handleArangoDbException(ArangoDBException arangoDBException) {
        if (arangoDBException.getResponseCode() == 400) {
            String message = arangoDBException.getMessage();
            if (message != null && message.contains("Invalid Name")) {
                String[] splitMessage = message.split("AQL:");
                String messageToShowOnFrontend = splitMessage[1];
                throw new GeneralBadRequestException(messageToShowOnFrontend);
            }
        }
    }

    public static void handleArangoDbException(RuntimeException runtimeException) {
        if (runtimeException instanceof ArangoDBException arangoDBException) {
            Integer responseCode = arangoDBException.getResponseCode();
            if (responseCode == HttpStatus.CONFLICT.value()) {
                throw new GeneralBadRequestException(arangoDBException.getErrorMessage());
            }
        }
    }
}
