package com.example.NewsComponent.configuration.arangoConfig;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.DocumentCreateOptions;
import com.example.NewsComponent.metadata.EdgeName;

import java.util.List;

import static com.example.NewsComponent.metadata.VertexName.namesAsList;

public class MetadataConfiguration {
    private final ArangoDatabase arangoDatabase;

    public MetadataConfiguration(ArangoDatabase arangoDatabase) {
        this.arangoDatabase = arangoDatabase;
    }

    public void createVertexCollections() {
        List<String> names = namesAsList();
        names.forEach(name -> {
            ArangoCollection arangoCollection = arangoDatabase.collection(name);
            if (!(arangoCollection.exists())) {
                arangoDatabase.createCollection(name);
            }
        });
    }

    public void createEdgeCollections() {
        List<String> names = EdgeName.namesAsList();
        names.forEach(name -> {
            ArangoCollection collection = arangoDatabase.collection(name);
            if (!(collection.exists())) {
                CollectionCreateOptions collectionCreateOptions = new CollectionCreateOptions();
                collectionCreateOptions.type(CollectionType.EDGES);
                arangoDatabase.createCollection(name, collectionCreateOptions);
            }
        });
    }
}
