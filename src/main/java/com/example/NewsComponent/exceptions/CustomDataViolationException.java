package com.example.NewsComponent.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;

public class CustomDataViolationException extends RuntimeException implements GraphQLError {

    private final Map<String, Object> extensions;

    public CustomDataViolationException(final String message, final Map<String, Object> extensions) {
        super(message);
        this.extensions = extensions;
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
        return extensions;
    }
}
