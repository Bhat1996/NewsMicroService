package com.example.NewsComponent.enums;

public enum FileType {
    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video"),
    PDF("pdf"),
    DOCUMENT("document");

    private final String type;
    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
