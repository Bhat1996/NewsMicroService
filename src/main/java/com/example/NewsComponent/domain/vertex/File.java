package com.example.NewsComponent.domain.vertex;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Map;

import static com.example.NewsComponent.metadata.VertexName.FILE;

@Document(FILE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    private String id;

    @ArangoId
    private String arangoId;

    private Map<String, String> seoTag;

    private Map<String, String> imageTitle;

    private String fileName;

    private String fileKey;

    private FileType fileType;
}