package com.example.NewsComponent;

import com.arangodb.springframework.annotation.EnableArangoRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableArangoRepositories
public class NewsComponentApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsComponentApplication.class, args);
	}

}
