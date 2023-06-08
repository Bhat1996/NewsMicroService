package com.example.NewsComponent.configuration.arango;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.ArangoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableArangoRepositories
public class ArangoConfig implements ArangoConfiguration {

    @Value("${arango.db.name}")
    private String dbName;

    @Value("${arango.db.host}")
    private String dbHost;

    @Value("${arango.db.port}")
    private Integer dbPort;

    @Value("${arango.db.user}")
    private String userName;

    @Value("${arango.db.password}")
    private String password;

    @Value("${arango.db.max.connection}")
    private Integer maxConnectionForPool;

    @Override
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder()
                .host(dbHost, dbPort)
                .user(userName)
                .password(password)
                .maxConnections(maxConnectionForPool);
    }

    @Override
    public String database() {
        return dbName;
    }


}
