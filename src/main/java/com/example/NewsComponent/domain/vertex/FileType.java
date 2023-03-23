package com.example.NewsComponent.domain.vertex;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;


import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class FileType {
    private List<LinkedHashMap<?, ?>> images;
    private List<LinkedHashMap<?, ?>> audios;
    private List<LinkedHashMap<?, ?>> videos;
    private List<LinkedHashMap<?, ?>> documents;

    private List<String> imageNames;
    private List<String> audioNames;
    private List<String> videoNames;
    private List<String> documentNames;

    public void setImages(List<LinkedHashMap<?, ?>> images) {
        this.images = images;
        if (CollectionUtils.isNotEmpty(images))
            imageNames = images.stream()
                    .map(linkedHashMap -> (String) linkedHashMap.get("name")).toList();
    }

    public void setAudios(List<LinkedHashMap<?, ?>> audios) {
        this.audios = audios;

        if (CollectionUtils.isNotEmpty(audios))
            audioNames = audios.stream()
                    .map(linkedHashMap -> (String) linkedHashMap.get("name")).toList();
    }

    public void setVideos(List<LinkedHashMap<?, ?>> videos) {
        this.videos = videos;

        if (CollectionUtils.isNotEmpty(videos))
            videoNames = videos.stream()
                    .map(linkedHashMap -> (String) linkedHashMap.get("name")).toList();
    }

    public void setDocuments(List<LinkedHashMap<?, ?>> documents) {
        this.documents = documents;

        if (CollectionUtils.isNotEmpty(documents))
            documentNames = documents.stream()
                    .map(linkedHashMap -> (String) linkedHashMap.get("name")).toList();
    }
}
