package com.example.NewsComponent.configuration;

import graphql.kickstart.servlet.apollo.ApolloScalars;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScalarConfiguration {

    @Bean
    public GraphQLScalarType longScalar() {
        return ExtendedScalars.GraphQLLong;
    }

    //    @Bean
//    public GraphQLScalarType dateScalar(){
//        return ExtendedScalars.Date;
//    }
//
//    @Bean
//    public JavaTimeModule dateTimeModule(){
//        return new JavaTimeModule();
//    }
//    @Bean
//    public GraphQLScalarType jsonScalar() {
//        return ExtendedScalars.Json;
//    }
    @Bean
    public GraphQLScalarType uploadScalars() {
        return ApolloScalars.Upload;
    }

    @Bean
    public GraphQLScalarType objectScalar() {
        return ExtendedScalars.Object;
    }


}
