package com.example.NewsComponent.repository;

import com.example.NewsComponent.domain.News;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface INewsRepository {

    public News getNewsById(String id );
}
