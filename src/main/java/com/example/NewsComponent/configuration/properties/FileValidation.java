package com.example.NewsComponent.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("file.validation")
@Getter
@Setter
public class FileValidation {
    private Integer numberOfImagesAllowed;
    private String maxImageSizeAllowed;
    private Integer numberOfAudiosAllowed;
    private String maxAudioSizeAllowed;
    private Integer numberOfVideosAllowed;
    private String maxVideoSizeAllowed;
    private Integer numberOfDocumentsAllowed;
    private String maxDocumentSizeAllowed;
    private String multipartRequestSizeAllowed;
    private String nonMultipartRequestSizeAllowed;
}
