package com.example.NewsComponent.dto.vertex;

import com.example.NewsComponent.enums.Status;
import com.example.NewsComponent.metadata.VertexName;
import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Document(VertexName.FILE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    private String id;
    @ArangoId
    private String arangoId;
    private Map<String, String> imageTitle;
    private Status status;
    private String fileName;
    private String fileKey;
    private String fileType;
}