package com.example.NewsComponent.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.http.HttpStatus;

import java.util.*;

public class CustomValidationException extends RuntimeException implements GraphQLError {

    private List<ValidationError> validationErrors;

    public CustomValidationException(String message) {
        super(message);
    }

    private static class ValidationError {
        private final String field;
        private final String message;

        ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }

    public void addValidationError(String field, String message){
        if(Objects.isNull(validationErrors)){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(field, message));
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
        Map<String, Object> extension = new HashMap<>();

        if (Objects.nonNull(validationErrors)) {
            validationErrors.forEach(validationError -> {
                if (extension.containsKey(validationError.field)) {
                    extension.put(validationError.field + " ", validationError.message);
                } else {
                    extension.put(validationError.field, validationError.message);
                }
            });
        }

        extension.put("status", HttpStatus.UNPROCESSABLE_ENTITY);
        extension.put("statusCode", HttpStatus.UNPROCESSABLE_ENTITY.value());
        return extension;
    }
}
