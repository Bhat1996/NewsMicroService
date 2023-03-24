package com.example.NewsComponent.mapper;

import com.example.NewsComponent.dto.request.FileInputWithPart;
import com.example.NewsComponent.dto.request.NewsRequest;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileInputMapper {

    private  final NewsRequest newsRequest;
    private final Map<String, List<Part>> parts;

    public FileInputMapper(NewsRequest newsRequest, Map<String, List<Part>> parts) {
        Objects.requireNonNull(newsRequest);
        Objects.requireNonNull(parts);

        this.newsRequest = newsRequest;
        this.parts = parts;
    }

    public FileInputWithPart getFileInputWithPart() {
        FileInputWithPart fileInputWithPart = new FileInputWithPart();

        setImageParts(fileInputWithPart);
        setVideoParts(fileInputWithPart);
        setAudioParts(fileInputWithPart);
        setDocumentParts(fileInputWithPart);

        return fileInputWithPart;
    }

    private void setImageParts(final FileInputWithPart fileInputWithPart) {
        List<String> imageNames = newsRequest.getFileInput().getImageNames();
        if (CollectionUtils.isEmpty(imageNames)) {
            fileInputWithPart.setImages(List.of());
            return;
        }
        List<Part> imageParts = imageNames.stream()
                .flatMap(name -> {
                    List<Part> partList = parts.get(name);
                    return partList.stream();
                }).toList();
        fileInputWithPart.setImages(imageParts);
    }

    private void setVideoParts(final FileInputWithPart fileInputWithPart) {
        List<String> videoNames = newsRequest.getFileInput().getVideoNames();
        if (CollectionUtils.isEmpty(videoNames)) {
            fileInputWithPart.setVideos(List.of());
            return;
        }
        List<Part> videoParts = videoNames.stream().flatMap(name -> {
            List<Part> partList = parts.get(name);
            return partList.stream();
        }).toList();
        fileInputWithPart.setVideos(videoParts);
    }

    private void setAudioParts(final FileInputWithPart fileInputWithPart) {
        List<String> audioNames = newsRequest.getFileInput().getAudioNames();
        if (CollectionUtils.isEmpty(audioNames)) {
            fileInputWithPart.setAudios(List.of());
            return;
        }
        List<Part> audioParts = audioNames.stream().flatMap(name -> {
            List<Part> partList = parts.get(name);
            return partList.stream();
        }).toList();
        fileInputWithPart.setAudios(audioParts);
    }

    private void setDocumentParts(final FileInputWithPart fileInputWithPart) {
        List<String> documentNames =newsRequest.getFileInput().getDocumentNames();
        if (CollectionUtils.isEmpty(documentNames)) {
            fileInputWithPart.setDocuments(List.of());
            return;
        }
        List<Part> documentParts = documentNames.stream().flatMap(name -> {
            List<Part> partList = parts.get(name);
            return partList.stream();
        }).toList();
        fileInputWithPart.setDocuments(documentParts);
    }
}
