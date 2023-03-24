package com.example.NewsComponent.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class InternalServerException extends RuntimeException implements GraphQLError {

    public InternalServerException() {
        super("Internal Server Error..!");
    }

    public InternalServerException(final String message) {
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
        return Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR,
                "statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
