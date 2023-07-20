package com.example.NewsComponent;

import com.example.NewsComponent.configuration.arango.MetadataConfiguration;
import com.arangodb.ArangoDatabase;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.core.ArangoOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.example.NewsComponent.configuration.properties")
@EnableArangoRepositories
@EnableCaching
public class NewsComponentApplication {

	@Value("${arango.db.name}")
	private String dbName;
	private final ArangoOperations arangoOperations;

	public NewsComponentApplication(ArangoOperations arangoOperations) {
		this.arangoOperations = arangoOperations;
	}

	public static void main(String[] args) {
		SpringApplication.run(NewsComponentApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void configureMetadata(){
		ArangoDatabase db = arangoOperations.driver().db(dbName);
		MetadataConfiguration metadataConfiguration = new MetadataConfiguration(db);
		metadataConfiguration.createVertexCollections();
		metadataConfiguration.createEdgeCollections();
		metadataConfiguration.newsSearchView();
	}

}
