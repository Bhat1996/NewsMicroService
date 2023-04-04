package com.example.NewsComponent.dto.response;

import java.util.Map;

public class GraphqlErrorResponse {
    private String message;
    private Map<String, Object> extensions;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
}
