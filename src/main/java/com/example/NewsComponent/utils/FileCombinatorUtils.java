package com.example.NewsComponent.utils;

import com.example.NewsComponent.domain.vertex.File;
import com.example.NewsComponent.dto.request.FileDto;
import com.example.NewsComponent.dto.request.FileKeyWithOriginalName;
import com.example.NewsComponent.enums.FileType;
import com.example.NewsComponent.enums.Status;

import java.util.ArrayList;
import java.util.List;

import static com.example.NewsComponent.enums.FileType.*;

public final class FileCombinatorUtils {
    private FileCombinatorUtils() {
        throw new UnsupportedOperationException("No Object For This Class..!");
    }

    public static List<File> getAllFilesToSave(final List<File> imageFilesToSave,
                                               final List<File> videoFilesToSave,
                                               final List<File> audioFilesToSave,
                                               final List<File> documentFilesToSave) {
        List<File> allFilesToSave = new ArrayList<>();
        allFilesToSave.addAll(imageFilesToSave);
        allFilesToSave.addAll(videoFilesToSave);
        allFilesToSave.addAll(audioFilesToSave);
        allFilesToSave.addAll(documentFilesToSave);
        return allFilesToSave;
    }

    public static List<File> getAllFilesToSave(final FileDto fileDto) {
        return getAllFilesToSave(
                getFilesToSave(fileDto.getImageKeys(), IMAGE),
                getFilesToSave(fileDto.getVideoKeys(), VIDEO),
                getFilesToSave(fileDto.getAudioKeys(), AUDIO),
                getFilesToSave(fileDto.getDocumentKeys(), DOCUMENT)
        );
    }

    private static List<File> getFilesToSave(final List<FileKeyWithOriginalName> fileKeyWithOriginalNames,
                                             final FileType fileType) {
        return fileKeyWithOriginalNames
                .stream()
                .map(fileKeyWithOriginalName -> {
                    File file = new File();
                    file.setFileKey(fileKeyWithOriginalName.fileKey());
                    file.setFileName(fileKeyWithOriginalName.fileOriginalName());
                    file.setFileType(fileType.getType());
                    file.setStatus(Status.ACTIVE);
                    return file;
                }).toList();
    }
}
