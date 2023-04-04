package com.example.NewsComponent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphqlResponseMapper<T> {
    private List<GraphqlErrorResponse> errors;
    private Map<String, T> data;

    public List<GraphqlErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<GraphqlErrorResponse> errors) {
        this.errors = errors;
    }

    public Map<String, T> getData() {
        return data;
    }

    public void setData(Map<String, T> data) {
        this.data = data;
    }

    public T result() {
        return data.get("result");
    }

    public T result(final String key) {
        return data.get(key);
    }
}
