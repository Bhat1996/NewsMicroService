package com.example.NewsComponent.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class CustomAccessDeniedException extends RuntimeException implements GraphQLError {

    public CustomAccessDeniedException(String message) {
        super(message);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Map.of("status", HttpStatus.FORBIDDEN, "statusCode", HttpStatus.FORBIDDEN.value());
    }
}
