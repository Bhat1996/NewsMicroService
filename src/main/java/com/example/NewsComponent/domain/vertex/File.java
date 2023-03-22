package com.example.NewsComponent.domain.vertex;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import static com.example.NewsComponent.metadata.VertexName.FILE;

@Document(FILE)
@Getter
@Setter
public class File {
    @Id
    private String id;

    @ArangoId
    private String arangoId;

    private String file;

    //private String fileKey;

    private String fileType;
}