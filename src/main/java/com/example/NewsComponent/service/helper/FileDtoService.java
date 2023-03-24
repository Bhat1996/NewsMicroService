package com.example.NewsComponent.service.helper;

import com.example.NewsComponent.dto.request.FileDto;
import com.example.NewsComponent.dto.request.FileInputWithPart;
import com.example.NewsComponent.dto.request.FileKeyWithOriginalName;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import java.util.List;

@Service
public class FileDtoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDtoService.class);

    private final S3BucketService s3BucketService;

    public FileDtoService(final S3BucketService s3BucketService) {
        this.s3BucketService = s3BucketService;
    }

    public FileDto getFileDto(final FileInputWithPart fileInputWithPart) {
        return getFileDto(fileInputWithPart.getImages(),
                fileInputWithPart.getAudios(),
                fileInputWithPart.getVideos(),
                fileInputWithPart.getDocuments());
    }



    @NotNull
    private FileDto getFileDto(List<Part> images,
                                       List<Part> audios,
                                       List<Part> videos,
                                       List<Part> documents) {
        FileDto fileDto = new FileDto();
        try {
            fileDto.setImageKeys(s3BucketService.saveParts(images));
            fileDto.setAudioKeys(s3BucketService.saveParts(audios));
            fileDto.setVideoKeys(s3BucketService.saveParts(videos));
            fileDto.setDocumentKeys(s3BucketService.saveParts(documents));

            return fileDto;
        } catch (IllegalStateException illegalStateException) {
            rollBackDto(fileDto);
            throw illegalStateException;
        }
    }

    @Async
    public void rollBackDto(FileDto fileDto) {
        LOGGER.info("Going to Rollback Saved Files to s3");
        fileDto.getImageKeys().forEach(this::rollBackFile);
        fileDto.getAudioKeys().forEach(this::rollBackFile);
        fileDto.getVideoKeys().forEach(this::rollBackFile);
        fileDto.getDocumentKeys().forEach(this::rollBackFile);
    }

    private void rollBackFile(FileKeyWithOriginalName fileKeyWithOriginalName) {
        String fileKey = fileKeyWithOriginalName.fileKey();
        LOGGER.info("Going For Deleting File as Rollback from S3, " +
                "File Key is {}", fileKey);
        s3BucketService.removePartSafely(fileKey);
    }
}
