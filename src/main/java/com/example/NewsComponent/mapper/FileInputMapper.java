package com.example.NewsComponent.mapper;

import com.example.NewsComponent.dto.request.FileInput;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;

public class FileInputMapper {

    private  final FileInput fileInput;
    private final Map<String, List<Part>> parts;

    public FileInputMapper(FileInput fileInput, Map<String, List<Part>> parts) {
        this.fileInput = fileInput;
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
        List<String> imageNames = fileInput.getImageNames();
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
        List<String> videoNames = fileInput.getVideoNames();
        if (CollectionUtils.isEmpty(videoNames)) {
            fileInput.setVideos(List.of());
            return;
        }
        List<Part> videoParts = videoNames.stream().flatMap(name -> {
            List<Part> partList = parts.get(name);
            return partList.stream();
        }).toList();
        fileInputWithPart.setVideos(videoParts);
    }

    private void setAudioParts(final FileInputWithPart fileInputWithPart) {
        List<String> audioNames = fileInput.getAudioNames();
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
        List<String> documentNames = fileInput.getDocumentNames();
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
