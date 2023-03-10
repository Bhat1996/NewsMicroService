package com.example.NewsComponent.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import com.example.NewsComponent.domain.News;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends ArangoRepository<News,String> ,INewsRepository {

}
